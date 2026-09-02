package com.coelho.manager.controller;

import com.coelho.manager.dto.SearchMunicipalitiesDTO;
import com.coelho.manager.model.Municipalities;
import com.coelho.manager.service.MunicipalitiesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/municipalities")
@AllArgsConstructor
public class MunicipalitiesController {

    MunicipalitiesService municipalitiesService;

    @GetMapping
    public ResponseEntity<?> getAllMunicipalities(){
        return municipalitiesService.gettAllMunicipalities();
    }

    @PostMapping("/list")
    public ResponseEntity<?> registerMunicipalitiesList(@RequestBody List<Municipalities> municipalitiesList){
        return municipalitiesService.registerMunicipalitiesList(municipalitiesList);
    }

    @PostMapping("/findbyname")
    public ResponseEntity<?> findMunicipalitiesByName(@RequestBody SearchMunicipalitiesDTO municipalitiesSearch){
        return municipalitiesService.findMunicipalitiesByName(municipalitiesSearch);
    }
}
