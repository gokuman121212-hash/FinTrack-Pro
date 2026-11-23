package com.fintrack.pro.infrastructure.agent;

import com.fasterxml.jackson.databind.JsonNode;

public interface Herramienta {
    String nombre();
    String descripcion();
    String esquema();
    String ejecutar(JsonNode entrada);
}