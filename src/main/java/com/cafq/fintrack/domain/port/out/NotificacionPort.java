package com.cafq.fintrack.domain.port.out;

public interface NotificacionPort {
    void notificarAlertaPresupuesto(String departamento, String mensajeAlerta);
}