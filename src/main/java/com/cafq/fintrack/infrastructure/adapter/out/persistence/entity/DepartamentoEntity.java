package com.cafq.fintrack.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "departamentos")
public class DepartamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String empresa;
    private String nombre;
    private BigDecimal presupuestoMensual;
    private BigDecimal gastoAcumulado;
    private boolean bloquearAlExceder;

    public DepartamentoEntity() {}

    public DepartamentoEntity(Long id, String empresa, String nombre, BigDecimal presupuestoMensual,
                              BigDecimal gastoAcumulado, boolean bloquearAlExceder) {
        this.id = id;
        this.empresa = empresa;
        this.nombre = nombre;
        this.presupuestoMensual = presupuestoMensual;
        this.gastoAcumulado = gastoAcumulado;
        this.bloquearAlExceder = bloquearAlExceder;
    }

    public Long getId() { return id; }
    public String getEmpresa() { return empresa; }
    public String getNombre() { return nombre; }
    public BigDecimal getPresupuestoMensual() { return presupuestoMensual; }
    public BigDecimal getGastoAcumulado() { return gastoAcumulado; }
    public boolean isBloquearAlExceder() { return bloquearAlExceder; }
}