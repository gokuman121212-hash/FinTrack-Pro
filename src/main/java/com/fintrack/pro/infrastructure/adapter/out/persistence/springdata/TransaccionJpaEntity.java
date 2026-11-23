package com.fintrack.pro.infrastructure.adapter.out.persistence.springdata;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class TransaccionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long departamentoId;
    private String tipo;
    private BigDecimal monto;
    private String categoria;
    private String descripcion;
    private LocalDateTime fecha;

    public TransaccionJpaEntity() {}
    public TransaccionJpaEntity(Long id, Long departamentoId, String tipo, BigDecimal monto,
                                String categoria, String descripcion, LocalDateTime fecha) {
        this.id = id;
        this.departamentoId = departamentoId;
        this.tipo = tipo;
        this.monto = monto;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }
    public Long getId() { return id; }
    public Long getDepartamentoId() { return departamentoId; }
    public String getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }
}