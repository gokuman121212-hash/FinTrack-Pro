package com.cafq.fintrack.domain.port.in;

import com.cafq.fintrack.domain.model.Departamento;
import java.math.BigDecimal;
import java.util.List;

public interface GestionarDepartamentoUseCase {
    Departamento crearDepartamento(String empresa, String nombre, BigDecimal presupuestoMensual, boolean bloquearAlExceder);
    List<Departamento> listarDepartamentos();
}