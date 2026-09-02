package com.coelho.manager.controller;

import com.coelho.manager.dto.SendMailRequestDTO;
import com.coelho.manager.service.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/mails")
public class MailSenderController {
    @Autowired
    MailService mailService;

    @PostMapping
    public ResponseEntity<?> sendMail(SendMailRequestDTO mailRequestDTO) throws MessagingException, IOException {
        return mailService.sendMail(mailRequestDTO);
    }
}
