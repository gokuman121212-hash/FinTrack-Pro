package com.fintrack.pro.application.service;

import com.fintrack.pro.domain.model.Departamento;
import com.fintrack.pro.domain.model.PresupuestoEstado;
import com.fintrack.pro.domain.model.Transaccion;
import com.fintrack.pro.domain.port.out.DepartamentoRepositoryPort;
import com.fintrack.pro.domain.port.out.TransaccionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class RegistrarTransaccionServiceTest {

    static class InMemoryDeptoRepo implements DepartamentoRepositoryPort {
        private final Map<Long, Departamento> store = new ConcurrentHashMap<>();
        private final AtomicLong seq = new AtomicLong(1);
        @Override public Departamento guardar(Departamento d) {
            if (d.getId() == null) d.setId(seq.getAndIncrement());
            store.put(d.getId(), d);
            return d;
        }
        @Override public Optional<Departamento> buscarPorId(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Departamento> buscarTodos() { return new ArrayList<>(store.values()); }
    }

    static class InMemoryTxRepo implements TransaccionRepositoryPort {
        private final List<Transaccion> list = new ArrayList<>();
        @Override public Transaccion guardar(Transaccion tx) { list.add(tx); return tx; }
        @Override public List<Transaccion> buscarTodas() { return list; }
    }

    @Test
    void debeDispararAlertaAl80PorCientoYRechazarMontoInvalido() {
        var deptoRepo = new InMemoryDeptoRepo();
        var txRepo = new InMemoryTxRepo();
        var registro = new RegistrarTransaccionService(txRepo, deptoRepo);
        var consulta = new ConsultarPresupuestoService(deptoRepo);

        var depto = consulta.crearDepartamento("TI", new BigDecimal("1000.00"));
        registro.registrar(depto.getId(), "EGRESO", new BigDecimal("850.00"), "Cloud", "VPS");

        PresupuestoEstado estado = consulta.consultarPorDepartamento(depto.getId());
        assertEquals("ALERTA (>=80%)", estado.alerta());

        assertThrows(IllegalArgumentException.class, () ->
                registro.registrar(depto.getId(), "EGRESO", new BigDecimal("-50.00"), "Cloud", "Negativo")
        );
    }
}