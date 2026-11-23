package com.cafq.fintrack.infrastructure.config;

import com.cafq.fintrack.application.service.FinTrackService;
import com.cafq.fintrack.domain.model.TipoTransaccion;
import com.cafq.fintrack.domain.port.out.DepartamentoRepositoryPort;
import com.cafq.fintrack.domain.port.out.NotificacionPort;
import com.cafq.fintrack.domain.port.out.TransaccionRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class HexagonalBeanConfig {

    @Bean
    public FinTrackService finTrackService(DepartamentoRepositoryPort deptoPort,
                                           TransaccionRepositoryPort txPort,
                                           NotificacionPort notifPort) {
        return new FinTrackService(deptoPort, txPort, notifPort);
    }

    @Bean
    public CommandLineRunner seedDemoData(FinTrackService service) {
        return args -> {
            if (service.listarDepartamentos().isEmpty()) {
                var d1 = service.crearDepartamento("TechNova SAC", "Desarrollo", new BigDecimal("15000.00"), true);
                var d2 = service.crearDepartamento("TechNova SAC", "Marketing", new BigDecimal("8000.00"), false);
                var d3 = service.crearDepartamento("TechNova SAC", "Operaciones", new BigDecimal("12000.00"), true);

                service.registrar(d1.getId(), TipoTransaccion.EGRESO, new BigDecimal("4200.00"), "Cloud AWS/VPS", "Infraestructura servidores", "admin@fintrack.io");
                service.registrar(d2.getId(), TipoTransaccion.EGRESO, new BigDecimal("6600.00"), "Publicidad Digital", "Campana Q4 Meta Ads", "finanzas@fintrack.io");
                service.registrar(d3.getId(), TipoTransaccion.EGRESO, new BigDecimal("3100.00"), "Logistica", "Equipamiento oficina Arequipa", "empleado@fintrack.io");
            }
        };
    }
}