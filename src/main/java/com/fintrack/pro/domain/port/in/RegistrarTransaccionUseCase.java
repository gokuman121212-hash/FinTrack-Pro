package com.fintrack.pro.domain.port.in;

import com.fintrack.pro.domain.model.Transaccion;
import java.math.BigDecimal;
import java.util.List;

public interface RegistrarTransaccionUseCase {
    Transaccion registrar(Long departamentoId, String tipo, BigDecimal monto, String categoria, String descripcion);
    List<Transaccion> listar();
}