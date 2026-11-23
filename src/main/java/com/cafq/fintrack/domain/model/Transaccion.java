package com.cafq.fintrack.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {
    private Long id;
    private Long departamentoId;
    private String departamentoNombre;
    private TipoTransaccion tipo;
    private BigDecimal monto;
    private String categoria;
    private String descripcion;
    private String registradoPor;
    private LocalDateTime fecha;
    private String alertaGenerada;

    public Transaccion(Long id, Long departamentoId, String departamentoNombre, TipoTransaccion tipo,
                       BigDecimal monto, String categoria, String descripcion, String registradoPor,
                       LocalDateTime fecha, String alertaGenerada) {
        this.id = id;
        this.departamentoId = departamentoId;
        this.departamentoNombre = departamentoNombre;
        this.tipo = tipo;
        this.monto = monto;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.registradoPor = registradoPor;
        this.fecha = fecha != null ? fecha : LocalDateTime.now();
        this.alertaGenerada = alertaGenerada;
    }

    public Long getId() { return id; }
    public Long getDepartamentoId() { return departamentoId; }
    public String getDepartamentoNombre() { return departamentoNombre; }
    public TipoTransaccion getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getRegistradoPor() { return registradoPor; }
    public LocalDateTime getFecha() { return fecha; }
    public String getAlertaGenerada() { return alertaGenerada; }
}