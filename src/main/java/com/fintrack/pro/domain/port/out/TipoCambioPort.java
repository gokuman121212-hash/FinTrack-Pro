package com.fintrack.pro.domain.port.out;

import java.math.BigDecimal;

public interface TipoCambioPort {
    BigDecimal obtenerTasaPenPorUsd();
}