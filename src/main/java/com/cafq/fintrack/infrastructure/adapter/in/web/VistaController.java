package com.cafq.fintrack.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class VistaController {

    @GetMapping("/api/vistas")
    public Map<String, Object> listarVistas() {
        return Map.of(
            "arquitectura", "Hexagonal + MVC / REST",
            "vistas", List.of(
                Map.of("nombre", "Portal / Mapa MVC", "ruta", "/index.html", "rol", "PUBLICO / TODOS"),
                Map.of("nombre", "Dashboard Administrador", "ruta", "/admin.html", "rol", "ROLE_ADMIN"),
                Map.of("nombre", "Dashboard Financiero", "ruta", "/financiero.html", "rol", "ROLE_FINANCIERO"),
                Map.of("nombre", "Dashboard Empleado", "ruta", "/empleado.html", "rol", "ROLE_EMPLEADO"),
                Map.of("nombre", "Notas y Arquitectura", "ruta", "/notas.html", "rol", "INTERNO")
            ),
            "endpoints", List.of(
                "POST /api/auth/login",
                "GET /api/departamentos",
                "POST /api/departamentos",
                "GET /api/transacciones",
                "POST /api/transacciones",
                "POST /api/agente/consultar",
                "GET /api/vistas"
            )
        );
    }
}