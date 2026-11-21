package com.cafq.fintrack.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioCuentaRepository extends JpaRepository<UsuarioCuenta, Long> {
    Optional<UsuarioCuenta> findByEmailIgnoreCase(String email);
}