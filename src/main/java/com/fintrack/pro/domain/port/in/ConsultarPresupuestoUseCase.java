package com.fintrack.pro.domain.port.in;

import com.fintrack.pro.domain.model.Departamento;
import com.fintrack.pro.domain.model.PresupuestoEstado;
import java.math.BigDecimal;
import java.util.List;

public interface ConsultarPresupuestoUseCase {
    Departamento crearDepartamento(String nombre, BigDecimal presupuestoMensual);
    List<Departamento> listarDepartamentos();
    PresupuestoEstado consultarPorDepartamento(Long departamentoId);
}