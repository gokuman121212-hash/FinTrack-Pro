package com.fintrack.pro.infrastructure.adapter.out.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaAgenteJpaRepository extends JpaRepository<AuditoriaAgenteEntity, Long> {
    List<AuditoriaAgenteEntity> findTop20ByOrderByIdDesc();
}