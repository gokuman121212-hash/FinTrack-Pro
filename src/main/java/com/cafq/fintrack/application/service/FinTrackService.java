package com.cafq.fintrack.application.service;

import com.cafq.fintrack.domain.exception.PresupuestoExcedidoException;
import com.cafq.fintrack.domain.model.*;
import com.cafq.fintrack.domain.port.in.GestionarDepartamentoUseCase;
import com.cafq.fintrack.domain.port.in.RegistrarTransaccionUseCase;
import com.cafq.fintrack.domain.port.out.DepartamentoRepositoryPort;
import com.cafq.fintrack.domain.port.out.NotificacionPort;
import com.cafq.fintrack.domain.port.out.TransaccionRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FinTrackService implements GestionarDepartamentoUseCase, RegistrarTransaccionUseCase {

    private final DepartamentoRepositoryPort departamentoRepository;
    private final TransaccionRepositoryPort transaccionRepository;
    private final NotificacionPort notificacionPort;

    public FinTrackService(DepartamentoRepositoryPort departamentoRepository,
                           TransaccionRepositoryPort transaccionRepository,
                           NotificacionPort notificacionPort) {
        this.departamentoRepository = departamentoRepository;
        this.transaccionRepository = transaccionRepository;
        this.notificacionPort = notificacionPort;
    }

    @Override
    public Departamento crearDepartamento(String empresa, String nombre, BigDecimal presupuestoMensual, boolean bloquearAlExceder) {
        Departamento cafqDepto = new Departamento(null, empresa, nombre, presupuestoMensual, BigDecimal.ZERO, bloquearAlExceder);
        return departamentoRepository.save(cafqDepto);
    }

    @Override
    public List<Departamento> listarDepartamentos() {
        return departamentoRepository.findAllDepartamentos();
    }

    @Override
    public Transaccion registrar(Long departamentoId, TipoTransaccion tipo, BigDecimal monto,
                                 String categoria, String descripcion, String usuario) {
        Departamento cafqDepto = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + departamentoId));

        cafqDepto.registrarMovimiento(tipo, monto);
        EstadoPresupuesto estado = cafqDepto.evaluarEstado();
        String alerta = null;

        if (tipo == TipoTransaccion.EGRESO) {
            if (estado == EstadoPresupuesto.EXCEDIDO_100) {
                String msg = String.format("CRITICO (100%%+): %s alcanzo %s%% de su presupuesto.",
                        cafqDepto.getNombre(), cafqDepto.calcularPorcentajeUso());
                notificacionPort.notificarAlertaPresupuesto(cafqDepto.getNombre(), msg);
                if (cafqDepto.isBloquearAlExceder()) {
                    throw new PresupuestoExcedidoException("Transaccion bloqueada por politica de sobregiro: " + msg);
                }
                alerta = msg;
            } else if (estado == EstadoPresupuesto.ADVERTENCIA_80) {
                alerta = String.format("ADVERTENCIA (80%%+): %s esta al %s%% de su limite mensual.",
                        cafqDepto.getNombre(), cafqDepto.calcularPorcentajeUso());
                notificacionPort.notificarAlertaPresupuesto(cafqDepto.getNombre(), alerta);
            }
        }

        departamentoRepository.save(cafqDepto);

        Transaccion cafqTx = new Transaccion(
                null,
                cafqDepto.getId(),
                cafqDepto.getNombre(),
                tipo,
                monto,
                categoria,
                descripcion,
                usuario,
                LocalDateTime.now(),
                alerta
        );
        return transaccionRepository.save(cafqTx);
    }

    @Override
    public List<Transaccion> listarTransacciones() {
        return transaccionRepository.findAllTransacciones();
    }
}