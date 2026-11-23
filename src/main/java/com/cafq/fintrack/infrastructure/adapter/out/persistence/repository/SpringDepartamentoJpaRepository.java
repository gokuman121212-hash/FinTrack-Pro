package com.cafq.fintrack.infrastructure.adapter.out.persistence.repository;

import com.cafq.fintrack.infrastructure.adapter.out.persistence.entity.DepartamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDepartamentoJpaRepository extends JpaRepository<DepartamentoEntity, Long> {
}