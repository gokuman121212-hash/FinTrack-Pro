package com.fintrack.pro.application.service;

import com.fintrack.pro.domain.model.Departamento;
import com.fintrack.pro.domain.model.PresupuestoEstado;
import com.fintrack.pro.domain.port.in.ConsultarPresupuestoUseCase;
import com.fintrack.pro.domain.port.out.DepartamentoRepositoryPort;

import java.math.BigDecimal;
import java.util.List;

public class ConsultarPresupuestoService implements ConsultarPresupuestoUseCase {

    private final DepartamentoRepositoryPort deptoPort;

    public ConsultarPresupuestoService(DepartamentoRepositoryPort deptoPort) {
        this.deptoPort = deptoPort;
    }

    @Override
    public Departamento crearDepartamento(String nombre, BigDecimal presupuestoMensual) {
        Departamento depto = new Departamento(null, nombre, presupuestoMensual, BigDecimal.ZERO);
        return deptoPort.guardar(depto);
    }

    @Override
    public List<Departamento> listarDepartamentos() {
        return deptoPort.buscarTodos();
    }

    @Override
    public PresupuestoEstado consultarPorDepartamento(Long departamentoId) {
        Departamento depto = deptoPort.buscarPorId(departamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + departamentoId));
        return depto.evaluarPresupuesto();
    }
}