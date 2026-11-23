package com.fintrack.pro.domain.port.out;

import com.fintrack.pro.domain.model.Transaccion;
import java.util.List;

public interface TransaccionRepositoryPort {
    Transaccion guardar(Transaccion transaccion);
    List<Transaccion> buscarTodas();
}