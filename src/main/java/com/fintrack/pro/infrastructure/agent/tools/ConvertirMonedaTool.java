package com.fintrack.pro.infrastructure.agent.tools;

import com.fintrack.pro.domain.port.out.TipoCambioPort;
import com.fintrack.pro.infrastructure.agent.Herramienta;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ConvertirMonedaTool implements Herramienta {

    private final TipoCambioPort tipoCambioPort;

    public ConvertirMonedaTool(TipoCambioPort tipoCambioPort) {
        this.tipoCambioPort = tipoCambioPort;
    }

    @Override
    public String nombre() {
        return "convertir_pen_a_usd";
    }

    @Override
    public String descripcion() {
        return "Convierte un monto en soles (PEN) a dolares (USD) usando el tipo de cambio oficial del sistema.";
    }

    @Override
    public String esquema() {
        return "{\"type\":\"object\",\"properties\":{\"montoPen\":{\"type\":\"number\"}},\"required\":[\"montoPen\"]}";
    }

    @Override
    public String ejecutar(JsonNode entrada) {
        if (!entrada.has("montoPen") || entrada.get("montoPen").isNull()) {
            throw new IllegalArgumentException("El parametro montoPen es obligatorio.");
        }
        BigDecimal pen = entrada.get("montoPen").decimalValue();
        BigDecimal tasa = tipoCambioPort.obtenerTasaPenPorUsd();
        BigDecimal usd = pen.divide(tasa, 2, RoundingMode.HALF_UP);
        return String.format("S/ %.2f equivalen a USD %.2f (Tasa: %.2f PEN/USD)", pen, usd, tasa);
    }
}