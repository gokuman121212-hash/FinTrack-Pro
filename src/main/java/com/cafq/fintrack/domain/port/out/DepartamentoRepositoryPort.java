package com.cafq.fintrack.domain.port.out;

import com.cafq.fintrack.domain.model.Departamento;
import java.util.List;
import java.util.Optional;

public interface DepartamentoRepositoryPort {
    Departamento save(Departamento departamento);
    Optional<Departamento> findById(Long id);
    List<Departamento> findAll();
}