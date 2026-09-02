package com.coelho.manager.repository;


import com.coelho.manager.model.ClientCircuit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientCircuitRepository extends JpaRepository<ClientCircuit, String> {
    public Optional<ClientCircuit> findByCid(String cid);
}
