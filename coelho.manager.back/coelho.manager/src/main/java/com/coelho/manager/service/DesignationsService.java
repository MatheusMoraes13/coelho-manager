package com.coelho.manager.service;

import com.coelho.manager.model.Designation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DesignationsService {

    public ResponseEntity<?> generateDesignation(Designation designation){
        String generatedDesignation = "";
        log.info("Gerando a designação");

        if (designation.getCNL() == null || designation.getContractId() == null || designation.getCircuitType() == null){
            log.error("Há campos inválidos ou não preenchidos, na entrada informada!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Erro, há campos inválidos ou não preenchidos, na entrada informada!");
        }

        try {
            generatedDesignation = designation.getCNL() + "000" + designation.getContractId() + designation.getCircuitType().getTypeAcronym();
            log.info("Designação gerada com sucesso!");
        } catch (RuntimeException e) {
            log.error("Erro ao gerar a designação {}", e.getMessage());
        }

        return ResponseEntity.ok(generatedDesignation);
    }
}
