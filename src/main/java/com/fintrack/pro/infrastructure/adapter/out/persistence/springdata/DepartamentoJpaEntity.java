package com.fintrack.pro.infrastructure.adapter.out.persistence.springdata;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "departamentos")
public class DepartamentoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private BigDecimal presupuestoMensual;
    private BigDecimal gastoAcumulado;

    public DepartamentoJpaEntity() {}
    public DepartamentoJpaEntity(Long id, String nombre, BigDecimal presupuestoMensual, BigDecimal gastoAcumulado) {
        this.id = id;
        this.nombre = nombre;
        this.presupuestoMensual = presupuestoMensual;
        this.gastoAcumulado = gastoAcumulado;
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPresupuestoMensual() { return presupuestoMensual; }
    public BigDecimal getGastoAcumulado() { return gastoAcumulado; }
}