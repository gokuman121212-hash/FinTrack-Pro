package com.fintrack.pro.infrastructure.adapter.out.persistence;

import com.fintrack.pro.domain.model.Transaccion;
import com.fintrack.pro.domain.port.out.TransaccionRepositoryPort;
import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.SpringDataTransaccionRepository;
import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.TransaccionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransaccionJpaAdapter implements TransaccionRepositoryPort {

    private final SpringDataTransaccionRepository repo;

    public TransaccionJpaAdapter(SpringDataTransaccionRepository repo) {
        this.repo = repo;
    }

    @Override
    public Transaccion guardar(Transaccion tx) {
        TransaccionJpaEntity saved = repo.save(new TransaccionJpaEntity(
                tx.getId(), tx.getDepartamentoId(), tx.getTipo(), tx.getMonto(),
                tx.getCategoria(), tx.getDescripcion(), tx.getFecha()
        ));
        return new Transaccion(saved.getId(), saved.getDepartamentoId(), saved.getTipo(),
                saved.getMonto(), saved.getCategoria(), saved.getDescripcion(), saved.getFecha());
    }

    @Override
    public List<Transaccion> buscarTodas() {
        return repo.findAll().stream()
                .map(e -> new Transaccion(e.getId(), e.getDepartamentoId(), e.getTipo(),
                        e.getMonto(), e.getCategoria(), e.getDescripcion(), e.getFecha()))
                .toList();
    }
}