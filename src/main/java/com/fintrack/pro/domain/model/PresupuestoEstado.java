package com.fintrack.pro.domain.model;

import java.math.BigDecimal;

public record PresupuestoEstado(
        Long departamentoId,
        String departamentoNombre,
        BigDecimal presupuestoMensual,
        BigDecimal gastoAcumulado,
        BigDecimal saldoDisponible,
        BigDecimal porcentajeUsado,
        String alerta
) {}