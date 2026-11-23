package com.fintrack.pro.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Departamento {
    private Long id;
    private String nombre;
    private BigDecimal presupuestoMensual;
    private BigDecimal gastoAcumulado;

    public Departamento(Long id, String nombre, BigDecimal presupuestoMensual, BigDecimal gastoAcumulado) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }
        if (presupuestoMensual == null || presupuestoMensual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El presupuesto mensual debe ser mayor a 0.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.presupuestoMensual = presupuestoMensual;
        this.gastoAcumulado = gastoAcumulado == null ? BigDecimal.ZERO : gastoAcumulado;
    }

    public void aplicarTransaccion(Transaccion tx) {
        if ("EGRESO".equals(tx.getTipo())) {
            this.gastoAcumulado = this.gastoAcumulado.add(tx.getMonto());
        } else {
            this.presupuestoMensual = this.presupuestoMensual.add(tx.getMonto());
        }
    }

    public PresupuestoEstado evaluarPresupuesto() {
        BigDecimal saldo = presupuestoMensual.subtract(gastoAcumulado);
        BigDecimal pct = gastoAcumulado
                .multiply(BigDecimal.valueOf(100))
                .divide(presupuestoMensual, 2, RoundingMode.HALF_UP);

        String alerta = "NORMAL";
        if (pct.compareTo(BigDecimal.valueOf(100)) > 0) {
            alerta = "SOBREGIRO (>=100%)";
        } else if (pct.compareTo(BigDecimal.valueOf(80)) >= 0) {
            alerta = "ALERTA (>=80%)";
        }
        return new PresupuestoEstado(id, nombre, presupuestoMensual, gastoAcumulado, saldo, pct, alerta);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPresupuestoMensual() { return presupuestoMensual; }
    public BigDecimal getGastoAcumulado() { return gastoAcumulado; }
}