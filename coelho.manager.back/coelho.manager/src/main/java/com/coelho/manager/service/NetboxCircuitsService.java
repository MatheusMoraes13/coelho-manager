package com.coelho.manager.service;

import com.coelho.manager.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${netbox.token}", url = "${netbox.url}", configuration = FeignClientConfig.class)
public interface NetboxCircuitsService {

    @GetMapping(value = "/api/circuits/circuits/?type=ccl&limit=1000")//?type=ccl
    String getCircuits();
}
