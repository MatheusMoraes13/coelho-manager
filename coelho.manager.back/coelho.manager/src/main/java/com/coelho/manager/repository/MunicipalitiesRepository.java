package com.coelho.manager.repository;

import com.coelho.manager.model.Municipalities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipalitiesRepository extends JpaRepository<Municipalities, Long> {
    public List<Municipalities> findByName(String name);
    public Optional<Municipalities> findByAcronym(String acronym);
    public Boolean existsByAcronym(String acronym);
}
