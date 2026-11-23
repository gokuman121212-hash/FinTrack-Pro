package com.fintrack.pro.infrastructure.adapter.in.web;

import com.fintrack.pro.infrastructure.agent.AgenteFinanciero;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agente")
public class AgenteFinancieroController {

    private final AgenteFinanciero agenteFinanciero;

    public AgenteFinancieroController(AgenteFinanciero agenteFinanciero) {
        this.agenteFinanciero = agenteFinanciero;
    }

    @PostMapping("/consultar")
    public Map<String, String> consultar(@RequestBody Map<String, String> body) {
        String pregunta = body.getOrDefault("pregunta", "Evalua el presupuesto de todos los departamentos.");
        return Map.of("respuesta", agenteFinanciero.consultar(pregunta));
    }
}