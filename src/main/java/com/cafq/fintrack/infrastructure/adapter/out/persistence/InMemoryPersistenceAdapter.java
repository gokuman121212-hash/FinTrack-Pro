package com.cafq.fintrack.infrastructure.adapter.out.persistence;

import com.cafq.fintrack.domain.model.Departamento;
import com.cafq.fintrack.domain.model.Transaccion;
import com.cafq.fintrack.domain.port.out.DepartamentoRepositoryPort;
import com.cafq.fintrack.domain.port.out.NotificacionPort;
import com.cafq.fintrack.domain.port.out.TransaccionRepositoryPort;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Adaptador secundario en memoria para pruebas unitarias ultrarrapidas sin levantar BD.
 */
public class InMemoryPersistenceAdapter implements DepartamentoRepositoryPort, TransaccionRepositoryPort, NotificacionPort {

    private final Map<Long, Departamento> deptos = new LinkedHashMap<>();
    private final List<Transaccion> txs = new ArrayList<>();
    private final List<String> alertas = new ArrayList<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public Departamento save(Departamento d) {
        if (d.getId() == null) d.setId(seq.getAndIncrement());
        deptos.put(d.getId(), d);
        return d;
    }

    @Override
    public Optional<Departamento> findById(Long id) {
        return Optional.ofNullable(deptos.get(id));
    }

    @Override
    public List<Departamento> findAll() {
        return new ArrayList<>(deptos.values());
    }

    @Override
    public Transaccion save(Transaccion t) {
        txs.add(t);
        return t;
    }

    @Override
    public List<Transaccion> findAll() {
        return new ArrayList<>(txs);
    }

    @Override
    public void notificarAlertaPresupuesto(String departamento, String mensajeAlerta) {
        alertas.add(departamento + ": " + mensajeAlerta);
    }

    public List<String> getAlertas() { return alertas; }
}