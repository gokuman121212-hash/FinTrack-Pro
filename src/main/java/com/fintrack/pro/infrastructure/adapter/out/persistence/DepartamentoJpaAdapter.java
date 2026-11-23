package com.fintrack.pro.infrastructure.adapter.out.persistence;

import com.fintrack.pro.domain.model.Departamento;
import com.fintrack.pro.domain.port.out.DepartamentoRepositoryPort;
import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.DepartamentoJpaEntity;
import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.SpringDataDepartamentoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DepartamentoJpaAdapter implements DepartamentoRepositoryPort {

    private final SpringDataDepartamentoRepository repo;

    public DepartamentoJpaAdapter(SpringDataDepartamentoRepository repo) {
        this.repo = repo;
    }

    @Override
    public Departamento guardar(Departamento d) {
        DepartamentoJpaEntity saved = repo.save(new DepartamentoJpaEntity(
                d.getId(), d.getNombre(), d.getPresupuestoMensual(), d.getGastoAcumulado()
        ));
        return new Departamento(saved.getId(), saved.getNombre(), saved.getPresupuestoMensual(), saved.getGastoAcumulado());
    }

    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        return repo.findById(id).map(e -> new Departamento(e.getId(), e.getNombre(), e.getPresupuestoMensual(), e.getGastoAcumulado()));
    }

    @Override
    public List<Departamento> buscarTodos() {
        return repo.findAll().stream()
                .map(e -> new Departamento(e.getId(), e.getNombre(), e.getPresupuestoMensual(), e.getGastoAcumulado()))
                .toList();
    }
}