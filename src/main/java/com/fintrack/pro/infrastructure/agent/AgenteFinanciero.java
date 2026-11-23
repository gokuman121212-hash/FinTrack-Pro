package com.fintrack.pro.infrastructure.agent;

import com.fintrack.pro.domain.model.Departamento;
import com.fintrack.pro.domain.port.in.ConsultarPresupuestoUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AgenteFinanciero {

    private static final int MAX_PASOS = 5;

    private final ClienteLlm clienteLlm;
    private final Map<String, Herramienta> herramientasPorNombre = new LinkedHashMap<>();
    private final List<Map<String, Object>> definicionesTools = new ArrayList<>();
    private final ConsultarPresupuestoUseCase presupuestoUseCase;
    private final ObjectMapper mapper = new ObjectMapper();

    public AgenteFinanciero(ClienteLlm clienteLlm,
                           List<Herramienta> herramientas,
                           ConsultarPresupuestoUseCase presupuestoUseCase) {
        this.clienteLlm = clienteLlm;
        this.presupuestoUseCase = presupuestoUseCase;
        for (Herramienta h : herramientas) {
            herramientasPorNombre.put(h.nombre(), h);
            try {
                definicionesTools.add(Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", h.nombre(),
                                "description", h.descripcion(),
                                "parameters", mapper.readTree(h.esquema())
                        )
                ));
            } catch (Exception ignored) {}
        }
    }

    public String consultar(String pregunta) {
        List<Map<String, Object>> historial = new ArrayList<>();
        historial.add(Map.of("role", "system", "content",
                "Eres el Agente Financiero de FinTrack Pro. Usa tus herramientas para consultar presupuestos o convertir moneda."));
        historial.add(Map.of("role", "user", "content", pregunta));

        try {
            for (int paso = 0; paso < MAX_PASOS; paso++) {
                JsonNode root = clienteLlm.completarConHerramientas(historial, definicionesTools);
                JsonNode msg = root.path("choices").get(0).path("message");
                JsonNode toolCalls = msg.path("tool_calls");

                if (toolCalls.isMissingNode() || !toolCalls.isArray() || toolCalls.isEmpty()) {
                    return msg.path("content").asText();
                }

                historial.add(Map.of(
                        "role", "assistant",
                        "content", msg.path("content").asText(""),
                        "tool_calls", mapper.convertValue(toolCalls, Object.class)
                ));

                for (JsonNode call : toolCalls) {
                    String toolName = call.path("function").path("name").asText();
                    String callId = call.path("id").asText("call_" + paso);
                    Herramienta tool = herramientasPorNombre.get(toolName);
                    if (tool == null) {
                        throw new IllegalArgumentException("Herramienta no registrada: " + toolName);
                    }
                    JsonNode args = mapper.readTree(call.path("function").path("arguments").asText("{}"));
                    String resultadoTool = tool.ejecutar(args);
                    historial.add(Map.of(
                            "role", "tool",
                            "tool_call_id", callId,
                            "name", toolName,
                            "content", resultadoTool
                    ));
                }
            }
            return "Se alcanzo el limite maximo de pasos del agente.";
        } catch (Exception ex) {
            Herramienta toolPresupuesto = herramientasPorNombre.get("consultar_presupuesto_departamento");
            List<Departamento> deptos = presupuestoUseCase.listarDepartamentos();
            StringBuilder sb = new StringBuilder("Analisis directo via Tool Calling:\n");
            for (Departamento d : deptos) {
                JsonNode input = mapper.createObjectNode().put("departamentoId", d.getId());
                sb.append("- ").append(toolPresupuesto.ejecutar(input)).append("\n");
            }
            return sb.toString();
        }
    }
}
