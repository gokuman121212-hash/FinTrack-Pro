package com.fintrack.pro.infrastructure.config;

import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.UsuarioSistemaEntity;
import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.UsuarioSistemaJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UsuarioSeedConfig {

    @Bean
    CommandLineRunner seedUsuariosPrueba(UsuarioSistemaJpaRepository repo) {
        return args -> {
            upsertUsuario(repo, "admin@fintrack.pro", "Admin2026#", "ADMIN");
            upsertUsuario(repo, "finanzas@fintrack.pro", "Finanzas2026#", "FINANCIERO");
            upsertUsuario(repo, "empleado@fintrack.pro", "Empleado2026#", "EMPLEADO");
        };
    }

    private void upsertUsuario(UsuarioSistemaJpaRepository repo, String username, String password, String rol) {
        UsuarioSistemaEntity entity = repo.findByUsernameIgnoreCaseAndActivoTrue(username)
                .orElseGet(UsuarioSistemaEntity::new);
        entity.setUsername(username);
        entity.setPasswordHash(password);
        entity.setRol(rol);
        entity.setActivo(true);
        repo.save(entity);
    }
}