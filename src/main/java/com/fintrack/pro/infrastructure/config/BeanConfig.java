package com.fintrack.pro.infrastructure.config;

import com.fintrack.pro.application.service.ConsultarPresupuestoService;
import com.fintrack.pro.application.service.RegistrarTransaccionService;
import com.fintrack.pro.domain.port.in.ConsultarPresupuestoUseCase;
import com.fintrack.pro.domain.port.in.RegistrarTransaccionUseCase;
import com.fintrack.pro.domain.port.out.DepartamentoRepositoryPort;
import com.fintrack.pro.domain.port.out.TransaccionRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class BeanConfig {

    @Bean
    public RegistrarTransaccionUseCase registrarTransaccionUseCase(TransaccionRepositoryPort txPort,
                                                                   DepartamentoRepositoryPort deptoPort) {
        return new RegistrarTransaccionService(txPort, deptoPort);
    }

    @Bean
    public ConsultarPresupuestoUseCase consultarPresupuestoUseCase(DepartamentoRepositoryPort deptoPort) {
        return new ConsultarPresupuestoService(deptoPort);
    }

    @Bean
    public CommandLineRunner cargarDatosDemo(ConsultarPresupuestoUseCase deptoUseCase,
                                            RegistrarTransaccionUseCase txUseCase) {
        return args -> {
            if (deptoUseCase.listarDepartamentos().isEmpty()) {
                var d1 = deptoUseCase.crearDepartamento("Ingenieria", new BigDecimal("10000.00"));
                var d2 = deptoUseCase.crearDepartamento("Marketing", new BigDecimal("6000.00"));
                var d3 = deptoUseCase.crearDepartamento("Operaciones", new BigDecimal("8000.00"));

                txUseCase.registrar(d1.getId(), "EGRESO", new BigDecimal("8200.00"), "Cloud", "Servidores y CI/CD");
                txUseCase.registrar(d2.getId(), "EGRESO", new BigDecimal("2400.00"), "Ads", "Campana digital");
                txUseCase.registrar(d3.getId(), "EGRESO", new BigDecimal("3500.00"), "Logistica", "Insumos operativos");
            }
        };
    }
}