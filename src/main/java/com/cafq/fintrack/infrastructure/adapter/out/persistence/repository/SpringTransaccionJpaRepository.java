package com.cafq.fintrack.infrastructure.adapter.out.persistence.repository;

import com.cafq.fintrack.infrastructure.adapter.out.persistence.entity.TransaccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringTransaccionJpaRepository extends JpaRepository<TransaccionEntity, Long> {
}