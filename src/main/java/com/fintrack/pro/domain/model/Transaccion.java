package com.fintrack.pro.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {
    private final Long id;
    private final Long departamentoId;
    private final String tipo; // INGRESO o EGRESO
    private final BigDecimal monto;
    private final String categoria;
    private final String descripcion;
    private final LocalDateTime fecha;

    public Transaccion(Long id, Long departamentoId, String tipo, BigDecimal monto,
                       String categoria, String descripcion, LocalDateTime fecha) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la transaccion debe ser mayor a 0.");
        }
        String tipoNorm = tipo == null ? "" : tipo.trim().toUpperCase();
        if (!"INGRESO".equals(tipoNorm) && !"EGRESO".equals(tipoNorm)) {
            throw new IllegalArgumentException("El tipo debe ser INGRESO o EGRESO.");
        }
        this.id = id;
        this.departamentoId = departamentoId;
        this.tipo = tipoNorm;
        this.monto = monto;
        this.categoria = categoria == null || categoria.isBlank() ? "General" : categoria.trim();
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.fecha = fecha == null ? LocalDateTime.now() : fecha;
    }

    public Long getId() { return id; }
    public Long getDepartamentoId() { return departamentoId; }
    public String getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }
}