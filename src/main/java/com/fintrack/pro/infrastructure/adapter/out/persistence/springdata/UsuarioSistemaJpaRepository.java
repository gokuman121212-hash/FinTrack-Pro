package com.fintrack.pro.infrastructure.adapter.out.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioSistemaJpaRepository extends JpaRepository<UsuarioSistemaEntity, Long> {
    Optional<UsuarioSistemaEntity> findByUsernameIgnoreCaseAndActivoTrue(String username);
}