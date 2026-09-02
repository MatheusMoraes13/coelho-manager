package com.coelho.manager.controller;

import com.coelho.manager.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coelho.manager.service.CircuitsService;

@RestController
@RequestMapping("/api/circuits")
@AllArgsConstructor
public class CircuitsController {

    CircuitsService circuitsService;
    MailService mailService;

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshCircuits() {
        return circuitsService.refreshCircuits();
    }

}
