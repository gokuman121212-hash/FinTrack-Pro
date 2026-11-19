package com.cafq.fintrack.domain.port.out;

import com.cafq.fintrack.domain.model.Transaccion;
import java.util.List;

public interface TransaccionRepositoryPort {
    Transaccion save(Transaccion transaccion);
    List<Transaccion> findAll();
}