package com.cafq.fintrack.domain.port.in;

import com.cafq.fintrack.domain.model.TipoTransaccion;
import com.cafq.fintrack.domain.model.Transaccion;
import java.math.BigDecimal;
import java.util.List;

public interface RegistrarTransaccionUseCase {
    Transaccion registrar(Long departamentoId, TipoTransaccion tipo, BigDecimal monto,
                          String categoria, String descripcion, String usuario);
    List<Transaccion> listarTransacciones();
}