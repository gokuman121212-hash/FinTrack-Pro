package com.fintrack.pro.infrastructure.adapter.out.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTransaccionRepository extends JpaRepository<TransaccionJpaEntity, Long> {}