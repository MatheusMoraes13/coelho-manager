package com.coelho.manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Entity
@Slf4j
@ToString
public class ClientCircuit {
    @Id
    String cid;
    String tenant;
    String vlan;
    String description;
    String comments;
    private String reportPattern;
    private String contractValue;

    public String CalcTotalValue(String valueMb, String usageValue, String usageUnit) {
        float total;
        float percentileValue = Float.parseFloat(usageValue);
        float valueMbValue = Float.parseFloat(valueMb);

        /*
        Realizando o cálculo do valor final a ser cobrado, com base na unidade de tráfego utilizada, realizando sempre a
        conversão para megas e multiplicando pelo valor definido por mega.
        */
        if (usageUnit.equalsIgnoreCase("GB")){
            total = valueMbValue * (percentileValue * 1000);
        } else if (usageUnit.equalsIgnoreCase("KB")) {
            total = valueMbValue * (percentileValue / 1000);
        } else {
            total = valueMbValue * percentileValue;
        }

        /*
        Modificando a saida para conter apenas dois números após a vírgula, e ser separado por ","
         */
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

        return df.format(total);
    }

    /*
    Função responsável por coletar as informações presentes no campo de comentários do circuito para, que sejam devidamente
    armazenadas no objeto.
     */
    public void collectCommentInformation(String comments) {
        try {
            // VLAN
            Matcher vlanMatcher = Pattern.compile("(?is)VLAN:\\*\\*\\s*`(\\d+)`").matcher(comments);
            if (vlanMatcher.find()) {
                this.vlan = vlanMatcher.group(1);
                log.info("VLAN armazenada com sucesso.");
                log.debug("VLAN encontrada: {}", this.vlan);
            }

            // Padrão de relatório
            Matcher reportMatcher = Pattern.compile("(?is)Padrão de relatório:\\*\\*\\s*`([^`]+)`").matcher(comments);
            if (reportMatcher.find()) {
                this.reportPattern = reportMatcher.group(1);
                log.info("Padrão de relatório armazenado com sucesso.");
                log.debug("Padrão de relatório encontrado: {}", this.reportPattern);
            }

            // Valor contrato
            Matcher valueMatcher = Pattern.compile("(?is)Valor contrato R\\$:\\*\\*\\s*`([0-9.,]+)`").matcher(comments);
            if (valueMatcher.find()) {
                this.contractValue = valueMatcher.group(1);
                log.info("Valor de contrato armazenado com sucesso.");
                log.debug("Valor de contrato encontrado: {}", this.contractValue);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao coletar informações dos comentários", e);
        }
    }

    /*
    Função para retornar o tipo de circuito definido no cadastro do cliente.
     */
    public String linkType(){
        if(Objects.equals(this.reportPattern, "ltip")){
            return "link transito ip";
        } else if(this.reportPattern.equals("cnd")){
            return "link transito cdn";
        } else {
            return "link ip";
        }
    }


    /*
    Função para retornar a designação formatada.
     */
    public String getDesignation(){
        return this.cid.replace("CLI - ", "");
    }

    public Map<String, String> getDates(){
        LocalDate today = LocalDate.now();
        LocalDate firstDayPreviousMonth = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayPreviousMonth = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<String, String> reportDates = new HashMap<>();

        reportDates.put("initialDate", firstDayPreviousMonth.format(dateTimeFormatter));
        reportDates.put("finalDate", lastDayPreviousMonth.format(dateTimeFormatter));

        return reportDates;
    }


    /*
    Função para retornar os endereços eletrónicos o qual irão receber o relatório de determinado cliente.
     */
    public String getMails(String defaultMailsPath) throws IOException {
        Matcher reportMatcher = Pattern.compile("(?is)Emails:\\*\\*\\s*`([^`]+)`").matcher(comments);
        Path path = new ClassPathResource(defaultMailsPath).getFile().toPath();
        String defaultMails = Files.readString(path);

        if (reportMatcher.find()) {
            String mails = reportMatcher.group(1) + ",";
            log.info("Emails retornados.");
            log.debug("Emails encontrados: {}", mails);

            return mails + defaultMails;
        } else  {
            log.info("Apenas os emails padões foram retornados.");
            return defaultMails;
        }
    }

    public String getFormattedMailTemplate(String template, String initialDate, String finalDate, String reportMonth){
        Pattern pattern = Pattern.compile("\\{\\{(.+?)}}");
        StringBuilder formattedTemplate = new StringBuilder();
        Matcher matcher = pattern.matcher(template);

        Map<String, String> replacements = new HashMap<>();

        replacements.put("{{CIRCUIT_TENANT}}", this.getTenant());
        replacements.put("{{INITIAL_DATE}}", initialDate);
        replacements.put("{{FINAL_DATE}}", finalDate);
        replacements.put("{{REPORT_MONTH}}", reportMonth);

        log.debug("Pupulando as variáveis do template.");
        while (matcher.find()){
            String key = matcher.group(1);
            String value = replacements.get(String.format("{{%s}}", key));

            if (value != null) {
                matcher.appendReplacement(formattedTemplate, Matcher.quoteReplacement(value));
            } else {
                log.warn("Nenhuma correspondência encontrada para: {}", key);
            }
        }

        matcher.appendTail(formattedTemplate);
        return formattedTemplate.toString();
    }


}
