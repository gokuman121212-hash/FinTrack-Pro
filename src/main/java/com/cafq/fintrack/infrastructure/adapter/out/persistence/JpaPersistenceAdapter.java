package com.cafq.fintrack.infrastructure.adapter.out.persistence;

import com.cafq.fintrack.domain.model.Departamento;
import com.cafq.fintrack.domain.model.Transaccion;
import com.cafq.fintrack.domain.port.out.DepartamentoRepositoryPort;
import com.cafq.fintrack.domain.port.out.NotificacionPort;
import com.cafq.fintrack.domain.port.out.TransaccionRepositoryPort;
import com.cafq.fintrack.infrastructure.adapter.out.persistence.entity.DepartamentoEntity;
import com.cafq.fintrack.infrastructure.adapter.out.persistence.entity.TransaccionEntity;
import com.cafq.fintrack.infrastructure.adapter.out.persistence.repository.SpringDepartamentoJpaRepository;
import com.cafq.fintrack.infrastructure.adapter.out.persistence.repository.SpringTransaccionJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaPersistenceAdapter implements DepartamentoRepositoryPort, TransaccionRepositoryPort, NotificacionPort {

    private static final Logger log = LoggerFactory.getLogger(JpaPersistenceAdapter.class);
    private final SpringDepartamentoJpaRepository deptoRepo;
    private final SpringTransaccionJpaRepository txRepo;

    public JpaPersistenceAdapter(SpringDepartamentoJpaRepository deptoRepo, SpringTransaccionJpaRepository txRepo) {
        this.deptoRepo = deptoRepo;
        this.txRepo = txRepo;
    }

    @Override
    public Departamento save(Departamento d) {
        DepartamentoEntity saved = deptoRepo.save(new DepartamentoEntity(
                d.getId(), d.getEmpresa(), d.getNombre(),
                d.getPresupuestoMensual(), d.getGastoAcumulado(), d.isBloquearAlExceder()
        ));
        return toDomain(saved);
    }

    @Override
    public Optional<Departamento> findById(Long id) {
        return deptoRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<Departamento> findAllDepartamentos() {
        return deptoRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Transaccion save(Transaccion t) {
        TransaccionEntity saved = txRepo.save(new TransaccionEntity(
                t.getId(), t.getDepartamentoId(), t.getDepartamentoNombre(), t.getTipo(),
                t.getMonto(), t.getCategoria(), t.getDescripcion(), t.getRegistradoPor(),
                t.getFecha(), t.getAlertaGenerada()
        ));
        return toDomain(saved);
    }

    @Override
    public List<Transaccion> findAllTransacciones() {
        return txRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void notificarAlertaPresupuesto(String departamento, String mensajeAlerta) {
        log.warn("[ALERTA FINTRACK - {}]: {}", departamento, mensajeAlerta);
    }

    private Departamento toDomain(DepartamentoEntity e) {
        return new Departamento(e.getId(), e.getEmpresa(), e.getNombre(),
                e.getPresupuestoMensual(), e.getGastoAcumulado(), e.isBloquearAlExceder());
    }

    private Transaccion toDomain(TransaccionEntity e) {
        return new Transaccion(e.getId(), e.getDepartamentoId(), e.getDepartamentoNombre(),
                e.getTipo(), e.getMonto(), e.getCategoria(), e.getDescripcion(),
                e.getRegistradoPor(), e.getFecha(), e.getAlertaGenerada());
    }
}