package com.cafq.fintrack.domain;

import com.cafq.fintrack.application.service.FinTrackService;
import com.cafq.fintrack.domain.exception.PresupuestoExcedidoException;
import com.cafq.fintrack.domain.model.EstadoPresupuesto;
import com.cafq.fintrack.domain.model.TipoTransaccion;
import com.cafq.fintrack.infrastructure.adapter.out.persistence.InMemoryPersistenceAdapter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FinTrackHexagonalTest {

    @Test
    void debeGenerarAlertaAl80PorCientoYBloquearAl100PorCiento() {
        var inMemoryAdapter = new InMemoryPersistenceAdapter();
        var service = new FinTrackService(inMemoryAdapter, inMemoryAdapter, inMemoryAdapter);

        var depto = service.crearDepartamento("TechNova SAC", "Desarrollo", new BigDecimal("1000.00"), true);

        // Gasto al 85% -> dispara alerta de advertencia
        var tx1 = service.registrar(depto.getId(), TipoTransaccion.EGRESO, new BigDecimal("850.00"), "Cloud", "Servidor", "cafq");
        assertNotNull(tx1.getAlertaGenerada());
        assertEquals(EstadoPresupuesto.ADVERTENCIA_80, depto.evaluarEstado());

        // Gasto adicional de 200 (105%) -> bloqueado por regla de negocio
        assertThrows(PresupuestoExcedidoException.class, () ->
                service.registrar(depto.getId(), TipoTransaccion.EGRESO, new BigDecimal("200.00"), "Cloud", "Exceso", "cafq")
        );
    }
}