package com.fintrack.pro.infrastructure.adapter.out.external;

import com.fintrack.pro.domain.port.out.TipoCambioPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TipoCambioHttpAdapter implements TipoCambioPort {
    @Override
    public BigDecimal obtenerTasaPenPorUsd() {
        return new BigDecimal("3.75");
    }
}