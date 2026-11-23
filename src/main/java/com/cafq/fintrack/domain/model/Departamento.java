package com.cafq.fintrack.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Departamento {
    private Long id;
    private String empresa;
    private String nombre;
    private BigDecimal presupuestoMensual;
    private BigDecimal gastoAcumulado;
    private boolean bloquearAlExceder;

    public Departamento(Long id, String empresa, String nombre, BigDecimal presupuestoMensual,
                        BigDecimal gastoAcumulado, boolean bloquearAlExceder) {
        this.id = id;
        this.empresa = empresa;
        this.nombre = nombre;
        this.presupuestoMensual = presupuestoMensual != null ? presupuestoMensual : BigDecimal.ZERO;
        this.gastoAcumulado = gastoAcumulado != null ? gastoAcumulado : BigDecimal.ZERO;
        this.bloquearAlExceder = bloquearAlExceder;
    }

    public BigDecimal calcularPorcentajeUso() {
        if (presupuestoMensual.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return gastoAcumulado
                .multiply(BigDecimal.valueOf(100))
                .divide(presupuestoMensual, 2, RoundingMode.HALF_UP);
    }

    public EstadoPresupuesto evaluarEstado() {
        BigDecimal pct = calcularPorcentajeUso();
        if (pct.compareTo(BigDecimal.valueOf(100)) > 0) {
            return EstadoPresupuesto.EXCEDIDO_100;
        }
        if (pct.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return EstadoPresupuesto.ADVERTENCIA_80;
        }
        return EstadoPresupuesto.SALUDABLE;
    }

    public void registrarMovimiento(TipoTransaccion tipo, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (tipo == TipoTransaccion.EGRESO) {
            this.gastoAcumulado = this.gastoAcumulado.add(monto);
        } else {
            this.presupuestoMensual = this.presupuestoMensual.add(monto);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmpresa() { return empresa; }
    public String getNombre() { return nombre; }
    public BigDecimal getPresupuestoMensual() { return presupuestoMensual; }
    public BigDecimal getGastoAcumulado() { return gastoAcumulado; }
    public boolean isBloquearAlExceder() { return bloquearAlExceder; }
}