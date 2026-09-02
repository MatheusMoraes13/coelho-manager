package com.coelho.manager.service;

import com.coelho.manager.model.ClientCircuit;
import com.coelho.manager.model.netbox.api.models.NetboxApiResponse;
import com.coelho.manager.repository.ClientCircuitRepository;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class CircuitsService {

    private final NetboxCircuitsService netboxCircuitsService;
    private ClientCircuitRepository clientCircuitRepository;

    public ResponseEntity<?> refreshCircuits() {
        log.info("Executando a api de atualização de Circuitos.");
        String response = "";
        Gson gson = new Gson();

        try {
            response = netboxCircuitsService.getCircuits();
            NetboxApiResponse netboxApiResponse = gson.fromJson(response, NetboxApiResponse.class);
            List<ClientCircuit> clientsToSave = netboxApiResponse.getResults().stream().map(c -> {
                ClientCircuit currentClientToSave = new ClientCircuit();
                    currentClientToSave.setCid(c.getCid());
                    Optional.ofNullable(c.getTenant()).flatMap(tenant -> Optional.ofNullable(tenant.getName())).ifPresent(currentClientToSave::setTenant);
                    currentClientToSave.setDescription(c.getDescription());
                    currentClientToSave.setComments(c.getComments());
                    currentClientToSave.collectCommentInformation(c.getComments());
                    return currentClientToSave;
                }).collect(Collectors.toList());

            clientCircuitRepository.saveAll(clientsToSave);
            return ResponseEntity.ok(netboxApiResponse);
        } catch (RuntimeException e) {
            log.error("Erro ao realizar a atualização dos circuitos retornados pelo Netbox: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a atualização dos circuitos retornados pelo Netbox: " + e.getMessage());
        }
    }
}
