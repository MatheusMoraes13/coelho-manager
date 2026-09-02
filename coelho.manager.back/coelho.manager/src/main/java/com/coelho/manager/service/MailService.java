package com.coelho.manager.service;

import com.coelho.manager.dto.SendMailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from}")
    private String sender;

    @Value("${mail.sender.template}")
    private String mailSenderTemplatePath;

    public void sendReportMail(String to, String subject, String body, String fileName, File file) throws MessagingException, FileNotFoundException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(sender);

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String[] recipients = to.split(",\\s*");

        helper.setTo(recipients);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.addAttachment(file.getClass().getName(),file);

        mailSender.send(message);
    }

    public ResponseEntity<?> sendMail(SendMailRequestDTO mailRequestDTO) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(sender);

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String[] recipients = mailRequestDTO.to().split(",\\s*");

        helper.setTo(recipients);
        helper.setSubject(mailRequestDTO.subject());
        helper.setText(populateMailContent(mailRequestDTO.subject(), mailRequestDTO.body()), true);
        if(mailRequestDTO.file().exists()){
            helper.addAttachment(mailRequestDTO.file().getName(),mailRequestDTO.file());
        }

        mailSender.send(message);

        return ResponseEntity.ok().body("Email enviado com sucesso.");
    }

    public String populateMailContent(String mailSubject, String mailContent) throws IOException {
        String mailTemplate = new String(
                new ClassPathResource(mailSenderTemplatePath).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        Pattern pattern = Pattern.compile("\\{\\{(.+?)}}");
        StringBuilder resultMail = new StringBuilder();
        Matcher matcher = pattern.matcher(mailTemplate);

        Map<String, String> replacements = new HashMap<>();

        replacements.put("{{MAIL_SUBJECT}}", mailSubject);
        replacements.put("{{MAIL_CONTENT}}", mailContent);

        while (matcher.find()){
            String key = matcher.group(1);
            String value = replacements.get(String.format("{{%s}}", key));

            if (value != null) {
                matcher.appendReplacement(resultMail, Matcher.quoteReplacement(value));
            } else {
                log.warn("Nenhuma correspondência encontrada para: {}", key);
            }
        }
        matcher.appendTail(resultMail);

        return resultMail.toString();
    }
}
