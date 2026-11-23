package com.fintrack.pro.domain.port.out;

import com.fintrack.pro.domain.model.Departamento;
import java.util.List;
import java.util.Optional;

public interface DepartamentoRepositoryPort {
    Departamento guardar(Departamento departamento);
    Optional<Departamento> buscarPorId(Long id);
    List<Departamento> buscarTodos();
}