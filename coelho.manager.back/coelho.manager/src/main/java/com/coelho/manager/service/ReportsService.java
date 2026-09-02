package com.coelho.manager.service;

import com.coelho.manager.dto.InterfaceInformationDTO;
import com.coelho.manager.model.ClientCircuit;
import com.coelho.manager.repository.ClientCircuitRepository;
import com.coelho.manager.service.function.ReportsServiceFunctions;
import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class ReportsService {
    @Value("${mail.report.template}")
    private String mailTemplatePath;

    @Value("${mail.to.default.path}")
    private String defaultMails;

    private static final String LIBRARY_BASE_PATH = "library/";
    private static final String ISP_TEMPLATE_BASE_PATH = "src/main/resources/templates/isp-client-form/";

    @Autowired
    ReportsServiceFunctions reportsFunctions;
    @Autowired
    MailService mailService;
    @Autowired
    ClientCircuitRepository clientCircuitRepository;

/*
Função responsável por realizar a leitura da imagem, através do OCR e extrair as informações do texto, com a utilização
da função de normalização dos dados.
*/
    public ResponseEntity<?> genReportIspPdf(MultipartFile trafficImage, String clientDesignation) throws TesseractException, IOException {
        Optional<ClientCircuit> clientCircuit = clientCircuitRepository.findByCid("CLI - " + clientDesignation);
        ClientCircuit clientToGenReport = new ClientCircuit();
        Map<String, String> reportDates = new HashMap<>();

        if(clientCircuit.isPresent()){
            log.info("Executando a API de geração de PDF para o cliente: {}", clientCircuit.get().getTenant());
            clientToGenReport = clientCircuit.get();
            log.debug("Cliente encontrado na base de dados: {}", clientToGenReport.toString());
            reportDates = clientToGenReport.getDates();
        } else {
            log.error("Nenhum circuito encontrado para a designação informada.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nenhum circuito encontrado, na base de dados, para a designação informada.");
        }

        /*
        Realizando a leitura dos textos da imagem enviada, através do OCR, e realizando a normalização dos dados obtidos.
         */
        log.info("Lendo a imagem encaminhada.");
        InterfaceInformationDTO dataOfImage = reportsFunctions.readImage(trafficImage);

        //Checando os diretórios de armazenamento dos arquivos.
        File baseDir = new File(LIBRARY_BASE_PATH);
        Files.createDirectories(Paths.get(LIBRARY_BASE_PATH));

        try {
            /*
            Definindo o diretório de saida, o template a ser utilizado e criando o renderizador de HTLM, que será
            responsável pela criação do PDF.
             */
            String BASE_OUTPUT_URL = new File(ISP_TEMPLATE_BASE_PATH).toURI().toURL().toString();
            String htmlContent = new String(Files.readAllBytes(Paths.get(ISP_TEMPLATE_BASE_PATH + "ispClientPDF.html")));
            ITextRenderer renderer = new ITextRenderer();

            //Definindo o nome do arquivo final.
            String reportFileName = reportsFunctions.genReportName(clientToGenReport.getTenant()) + ".pdf";
            String outPutFile = "library/" + reportFileName;


            /*
            Realizando a troca dos valores do HTML para que contenha os valores enviados na requisição, nessa função foi
            utilizado o modelo com Pattern compilado e o Matcher garantindo uma implementação mais rápida.
             */
            Pattern pattern = Pattern.compile("\\{\\{(.+?)}}");
            StringBuilder resultHtml = new StringBuilder();
            Matcher matcher = pattern.matcher(htmlContent);

            Map<String, String> replacements = new HashMap<>();

            replacements.put("{{CLIENT_NAME}}", clientToGenReport.getTenant());
            replacements.put("{{INITIAL_DATE}}", reportDates.get("initialDate"));
            replacements.put("{{FINAL_DATE}}", reportDates.get("finalDate"));
            replacements.put("{{CLIENT_LINK}}", clientToGenReport.linkType());
            replacements.put("{{CIRCUIT_DESIGNATION}}", clientToGenReport.getDesignation());
            replacements.put("{{CIRCUIT_VLAN}}", clientToGenReport.getVlan());
            replacements.put("{{VALUE_MB}}", reportsFunctions.valueFormatter(clientToGenReport.getContractValue()));
            replacements.put("{{PERCENTILE}}", dataOfImage.percentile());
            replacements.put("{{PERCENTILE_UNIT}}", dataOfImage.percentileUnit());
            replacements.put("{{TOTAL_VALUE}}", reportsFunctions.calcTotalValue(clientToGenReport.getContractValue(), dataOfImage.percentile(), dataOfImage.percentileUnit()));

            log.debug("Realizando a normalização dos dados retornados pelo OCR.");
            while (matcher.find()){
                String key = matcher.group(1);
                String value = replacements.get(String.format("{{%s}}", key));

                if (value != null) {
                    matcher.appendReplacement(resultHtml, Matcher.quoteReplacement(value));
                } else {
                    log.warn("Nenhuma correspondência encontrada para: {}", key);
                }
            }
            matcher.appendTail(resultHtml);
            log.info("Normalização dos dados retornados pelo OCR foi finalizada!");

            try (FileOutputStream outputStream = new FileOutputStream(outPutFile)) {
                log.info("Gerando o relatório");

                renderer.setDocumentFromString(resultHtml.toString(), BASE_OUTPUT_URL);
                renderer.layout();
                renderer.createPDF(outputStream);

                log.info("PDF de relatório gerado com sucesso para o cliente {}.", clientToGenReport.getTenant());
            }

            /*
            Retornando o PDF para o endpoint da requisição e enviando o endereço eletrónico de acordo o com o mapeado no objeto.
             */
            Path reportPdfPath = Paths.get(outPutFile);
            byte[] reportPdfBytes = Files.readAllBytes(reportPdfPath);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", new Locale("pt", "BR"));
            String mailTemplate = new String(
                    new ClassPathResource(mailTemplatePath).getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            mailService.sendReportMail(
                    clientToGenReport.getMails(defaultMails),
                    "Relatório de tráfego " + clientToGenReport.getTenant() + " - " +
                            LocalDateTime.now().minusMonths(1).format(formatter).toUpperCase(),
                    clientToGenReport.getFormattedMailTemplate(
                            mailTemplate,
                            reportDates.get("initialDate"),
                            reportDates.get("finalDate"),
                            LocalDateTime.now().minusMonths(1).format(formatter).toLowerCase()
                    ), reportsFunctions.genReportName(clientToGenReport.getTenant()),
                    new File(outPutFile)
            );

            HttpHeaders reportResponseHeaders = new HttpHeaders();
            reportResponseHeaders.setContentType(MediaType.APPLICATION_PDF);
            reportResponseHeaders.setContentDispositionFormData("attachment", reportFileName);
            reportResponseHeaders.setContentLength(reportPdfBytes.length);

            return new ResponseEntity<>(reportPdfBytes, reportResponseHeaders, HttpStatus.OK);
        } catch (IOException | DocumentException | MessagingException e){
            log.error("Erro ao gerar o relatório em PDF.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar e encaminhar o relatório em PDF" + e.getMessage());
        }
    }

}