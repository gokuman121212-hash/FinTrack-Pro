package com.cafq.fintrack.infrastructure.adapter.out.ai;

import com.cafq.fintrack.domain.model.Departamento;
import com.cafq.fintrack.domain.port.in.GestionarDepartamentoUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinTrackCopilotAgent {

    private final GestionarDepartamentoUseCase departamentoUseCase;
    private final RestClient restClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${fintrack.ai.endpoint}")
    private String aiEndpoint;

    @Value("${fintrack.ai.api-key}")
    private String aiApiKey;

    @Value("${fintrack.ai.model}")
    private String aiModel;

    public FinTrackCopilotAgent(GestionarDepartamentoUseCase departamentoUseCase) {
        this.departamentoUseCase = departamentoUseCase;
        this.restClient = RestClient.create();
    }

    /**
     * Tool nativa en Java expuesta al LLM mediante Tool Calling.
     */
    public String consultarPresupuestosTool() {
        List<Departamento> lista = departamentoUseCase.listarDepartamentos();
        if (lista.isEmpty()) return "[]";
        return lista.stream()
                .map(d -> String.format(
                        "{\"departamento\":\"%s\",\"empresa\":\"%s\",\"presupuesto\":%.2f,\"gastado\":%.2f,\"porcentajeUso\":%.2f,\"estado\":\"%s\"}",
                        d.getNombre(), d.getEmpresa(), d.getPresupuestoMensual(),
                        d.getGastoAcumulado(), d.calcularPorcentajeUso(), d.evaluarEstado()
                ))
                .collect(Collectors.joining(",", "[", "]"));
    }

    public Map<String, Object> responderConsulta(String preguntaUsuario) {
        String datosTool = consultarPresupuestosTool();

        // Definicion formal de la herramienta para protocolo OpenAI / Ollama Tool Calling
        Map<String, Object> toolDefinition = Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "consultarPresupuestosTool",
                        "description", "Obtiene el estado presupuestario actual, gastos acumulados y porcentaje de uso de todos los departamentos.",
                        "parameters", Map.of("type", "object", "properties", Map.of())
                )
        );

        try {
            Map<String, Object> payload = Map.of(
                    "model", aiModel,
                    "messages", List.of(
                            Map.of("role", "system", "content", "Eres el Copiloto Financiero Ejecutivo de FinTrack Pro. Usa la herramienta consultarPresupuestosTool para responder con precision y brevedad."),
                            Map.of("role", "user", "content", preguntaUsuario)
                    ),
                    "tools", List.of(toolDefinition)
            );

            String rawResponse = restClient.post()
                    .uri(aiEndpoint)
                    .header("Authorization", "Bearer " + aiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            JsonNode root = mapper.readTree(rawResponse);
            JsonNode message = root.path("choices").get(0).path("message");

            // Si el modelo decide invocar la herramienta, ejecutamos el segundo turno con el resultado
            if (message.has("tool_calls") && message.get("tool_calls").size() > 0) {
                String toolCallId = message.get("tool_calls").get(0).path("id").asText("call_1");
                Map<String, Object> secondPayload = Map.of(
                        "model", aiModel,
                        "messages", List.of(
                                Map.of("role", "system", "content", "Redacta un reporte ejecutivo claro en espanol basado en los datos de la herramienta."),
                                Map.of("role", "user", "content", preguntaUsuario),
                                Map.of("role", "assistant", "content", "", "tool_calls", mapper.convertValue(message.get("tool_calls"), Object.class)),
                                Map.of("role", "tool", "tool_call_id", toolCallId, "name", "consultarPresupuestosTool", "content", datosTool)
                        )
                );

                String finalRaw = restClient.post()
                        .uri(aiEndpoint)
                        .header("Authorization", "Bearer " + aiApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(secondPayload)
                        .retrieve()
                        .body(String.class);

                String answer = mapper.readTree(finalRaw).path("choices").get(0).path("message").path("content").asText();
                return Map.of("modo", "LLM_TOOL_CALLING", "toolEjecutada", "consultarPresupuestosTool", "respuesta", answer);
            }

            return Map.of("modo", "LLM_DIRECTO", "toolEjecutada", "consultarPresupuestosTool", "respuesta", message.path("content").asText());

        } catch (Exception ex) {
            // Fallback inteligente deterministico si Ollama no esta encendido durante la demo
            List<Departamento> deptos = departamentoUseCase.listarDepartamentos();
            StringBuilder reporte = new StringBuilder("Reporte Ejecutivo (Tool: consultarPresupuestosTool):\n");
            for (Departamento d : deptos) {
                reporte.append(String.format("- %s (%s): Gastado S/ %.2f de S/ %.2f (%.1f%%) -> Estado: %s\n",
                        d.getNombre(), d.getEmpresa(), d.getGastoAcumulado(),
                        d.getPresupuestoMensual(), d.calcularPorcentajeUso(), d.evaluarEstado()));
            }
            return Map.of(
                    "modo", "FALLBACK_LOCAL_TOOL",
                    "toolEjecutada", "consultarPresupuestosTool",
                    "datosCrudos", datosTool,
                    "respuesta", reporte.toString()
            );
        }
    }
}