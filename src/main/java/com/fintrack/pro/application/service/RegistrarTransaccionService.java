package com.fintrack.pro.application.service;

import com.fintrack.pro.domain.model.Departamento;
import com.fintrack.pro.domain.model.Transaccion;
import com.fintrack.pro.domain.port.in.RegistrarTransaccionUseCase;
import com.fintrack.pro.domain.port.out.DepartamentoRepositoryPort;
import com.fintrack.pro.domain.port.out.TransaccionRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RegistrarTransaccionService implements RegistrarTransaccionUseCase {

    private final TransaccionRepositoryPort txPort;
    private final DepartamentoRepositoryPort deptoPort;

    public RegistrarTransaccionService(TransaccionRepositoryPort txPort, DepartamentoRepositoryPort deptoPort) {
        this.txPort = txPort;
        this.deptoPort = deptoPort;
    }

    @Override
    public Transaccion registrar(Long departamentoId, String tipo, BigDecimal monto, String categoria, String descripcion) {
        Departamento depto = deptoPort.buscarPorId(departamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + departamentoId));

        Transaccion tx = new Transaccion(null, departamentoId, tipo, monto, categoria, descripcion, LocalDateTime.now());
        depto.aplicarTransaccion(tx);
        deptoPort.guardar(depto);
        return txPort.guardar(tx);
    }

    @Override
    public List<Transaccion> listar() {
        return txPort.buscarTodas();
    }
}