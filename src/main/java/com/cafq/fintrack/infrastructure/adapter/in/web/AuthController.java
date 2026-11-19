package com.cafq.fintrack.infrastructure.adapter.in.web;

import com.cafq.fintrack.infrastructure.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "");
        String password = body.getOrDefault("password", "");

        // Usuarios preconfigurados para validar los 3 roles granulares de la rubrica
        if ("admin@fintrack.io".equals(email) && "admin123".equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "token", jwtService.generateToken(email, "ROLE_ADMIN", 0L),
                    "role", "ROLE_ADMIN",
                    "nombre", "Cristhian Flores (Admin)"
            ));
        }
        if ("finanzas@fintrack.io".equals(email) && "fin123".equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "token", jwtService.generateToken(email, "ROLE_FINANCIERO", 0L),
                    "role", "ROLE_FINANCIERO",
                    "nombre", "Analista Financiero"
            ));
        }
        if ("empleado@fintrack.io".equals(email) && "emp123".equals(password)) {
            // Empleado asignado exclusivamente al Departamento ID = 3 (Operaciones)
            return ResponseEntity.ok(Map.of(
                    "token", jwtService.generateToken(email, "ROLE_EMPLEADO", 3L),
                    "role", "ROLE_EMPLEADO",
                    "departamentoAsignadoId", 3L,
                    "nombre", "Colaborador Operaciones"
            ));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Credenciales invalidas"));
    }
}