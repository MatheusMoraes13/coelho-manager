package com.coelho.manager.repository;

import com.coelho.manager.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationsRepository extends JpaRepository<Designation, Long> {
}
