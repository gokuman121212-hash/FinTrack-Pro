package com.fintrack.pro.infrastructure.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class ClienteLlm {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String url;
    private final String apiKey;
    private final String model;

    public ClienteLlm(@Value("${llm.url}") String url,
                      @Value("${llm.api-key}") String apiKey,
                      @Value("${llm.model}") String model) {
        this.url = url;
        this.apiKey = apiKey;
        this.model = model;
    }

    public JsonNode completarConHerramientas(List<Map<String, Object>> mensajes, List<Map<String, Object>> tools) {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", mensajes,
                "tools", tools
        );
        String raw = restClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        try {
            return mapper.readTree(raw);
        } catch (Exception e) {
            throw new RuntimeException("Error parseando respuesta del LLM", e);
        }
    }
}
