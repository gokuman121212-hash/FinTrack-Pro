package com.fintrack.pro.infrastructure.agent.tools;

import com.fintrack.pro.domain.model.PresupuestoEstado;
import com.fintrack.pro.domain.port.in.ConsultarPresupuestoUseCase;
import com.fintrack.pro.infrastructure.agent.Herramienta;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class PresupuestoDepartamentoTool implements Herramienta {

    private final ConsultarPresupuestoUseCase consultarPresupuestoUseCase;

    public PresupuestoDepartamentoTool(ConsultarPresupuestoUseCase consultarPresupuestoUseCase) {
        this.consultarPresupuestoUseCase = consultarPresupuestoUseCase;
    }

    @Override
    public String nombre() {
        return "consultar_presupuesto_departamento";
    }

    @Override
    public String descripcion() {
        return "Consulta presupuesto mensual, gasto acumulado, saldo disponible, porcentaje usado y alerta de un departamento.";
    }

    @Override
    public String esquema() {
        return "{\"type\":\"object\",\"properties\":{\"departamentoId\":{\"type\":\"integer\"}},\"required\":[\"departamentoId\"]}";
    }

    @Override
    public String ejecutar(JsonNode entrada) {
        if (!entrada.has("departamentoId") || entrada.get("departamentoId").isNull()) {
            throw new IllegalArgumentException("El parametro departamentoId es obligatorio.");
        }
        long id = entrada.get("departamentoId").asLong();
        PresupuestoEstado estado = consultarPresupuestoUseCase.consultarPorDepartamento(id);
        return String.format(
                "Departamento: %s (ID %d) | Presupuesto: S/ %.2f | Gastado: S/ %.2f | Saldo: S/ %.2f | Uso: %.2f%% | Estado: %s",
                estado.departamentoNombre(), estado.departamentoId(), estado.presupuestoMensual(),
                estado.gastoAcumulado(), estado.saldoDisponible(), estado.porcentajeUsado(), estado.alerta()
        );
    }
}