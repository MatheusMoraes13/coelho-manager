package com.coelho.manager.dto;

import java.io.File;

public record SendMailRequestDTO(String to, String subject, String body, File file) {}
