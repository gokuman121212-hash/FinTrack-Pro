package com.cafq.fintrack.infrastructure.adapter.in.web;

import com.cafq.fintrack.domain.exception.PresupuestoExcedidoException;
import com.cafq.fintrack.domain.model.Departamento;
import com.cafq.fintrack.domain.model.TipoTransaccion;
import com.cafq.fintrack.domain.model.Transaccion;
import com.cafq.fintrack.domain.port.in.GestionarDepartamentoUseCase;
import com.cafq.fintrack.domain.port.in.RegistrarTransaccionUseCase;
import com.cafq.fintrack.infrastructure.adapter.out.ai.FinTrackCopilotAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FinTrackController {

    private final GestionarDepartamentoUseCase deptoUseCase;
    private final RegistrarTransaccionUseCase txUseCase;
    private final FinTrackCopilotAgent copilotAgent;

    public FinTrackController(GestionarDepartamentoUseCase deptoUseCase,
                              RegistrarTransaccionUseCase txUseCase,
                              FinTrackCopilotAgent copilotAgent) {
        this.deptoUseCase = deptoUseCase;
        this.txUseCase = txUseCase;
        this.copilotAgent = copilotAgent;
    }

    @GetMapping("/departamentos")
    public List> listarDepartamentos() {
        return deptoUseCase.listarDepartamentos().stream().map(d -> Map.<String, Object>of(
                "id", d.getId(),
                "empresa", d.getEmpresa(),
                "nombre", d.getNombre(),
                "presupuestoMensual", d.getPresupuestoMensual(),
                "gastoAcumulado", d.getGastoAcumulado(),
                "porcentajeUso", d.calcularPorcentajeUso(),
                "estado", d.evaluarEstado(),
                "bloquearAlExceder", d.isBloquearAlExceder()
        )).toList();
    }

    @PostMapping("/departamentos")
    public Departamento crearDepartamento(@RequestBody Map<String, Object> body) {
        String empresa = (String) body.getOrDefault("empresa", "TechNova SAC");
        String nombre = (String) body.get("nombre");
        BigDecimal presupuesto = new BigDecimal(body.get("presupuestoMensual").toString());
        boolean bloquear = Boolean.parseBoolean(body.getOrDefault("bloquearAlExceder", true).toString());
        return deptoUseCase.crearDepartamento(empresa, nombre, presupuesto, bloquear);
    }

    @GetMapping("/transacciones")
    public List<Transaccion> listarTransacciones() {
        return txUseCase.listarTransacciones();
    }

    @PostMapping("/transacciones")
    public ResponseEntity<?> registrarTransaccion(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            Long deptoId = Long.valueOf(body.get("departamentoId").toString());

            // Validacion granular para ROLE_EMPLEADO: solo puede registrar en su propio departamento
            boolean esEmpleado = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
            if (esEmpleado) {
                Long deptoPermitido = (Long) auth.getCredentials();
                if (!deptoId.equals(deptoPermitido)) {
                    return ResponseEntity.status(403).body(Map.of(
                            "error", "Acceso denegado: ROLE_EMPLEADO solo puede registrar gastos en su departamento asignado (ID: " + deptoPermitido + ")."
                    ));
                }
            }

            TipoTransaccion tipo = TipoTransaccion.valueOf(body.get("tipo").toString().toUpperCase());
            BigDecimal monto = new BigDecimal(body.get("monto").toString());
            String categoria = body.getOrDefault("categoria", "General").toString();
            String descripcion = body.getOrDefault("descripcion", "").toString();

            Transaccion tx = txUseCase.registrar(deptoId, tipo, monto, categoria, descripcion, auth.getName());
            return ResponseEntity.ok(tx);
        } catch (PresupuestoExcedidoException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/copilot/chat")
    public Map<String, Object> consultarCopiloto(@RequestBody Map<String, String> body) {
        String pregunta = body.getOrDefault("pregunta", "¿Qué departamento está cerca de agotar su presupuesto este mes?");
        return copilotAgent.responderConsulta(pregunta);
    }
}