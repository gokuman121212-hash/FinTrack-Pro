package com.fintrack.pro.infrastructure.adapter.in.web;

import com.fintrack.pro.domain.model.Departamento;
import com.fintrack.pro.domain.model.PresupuestoEstado;
import com.fintrack.pro.domain.port.in.ConsultarPresupuestoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final ConsultarPresupuestoUseCase consultarPresupuestoUseCase;

    public DepartamentoController(ConsultarPresupuestoUseCase consultarPresupuestoUseCase) {
        this.consultarPresupuestoUseCase = consultarPresupuestoUseCase;
    }

    @GetMapping
    public List<PresupuestoEstado> listar() {
        return consultarPresupuestoUseCase.listarDepartamentos().stream()
                .map(Departamento::evaluarPresupuesto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            String nombre = String.valueOf(body.get("nombre"));
            BigDecimal presupuesto = new BigDecimal(String.valueOf(body.get("presupuestoMensual")));
            Departamento creado = consultarPresupuestoUseCase.crearDepartamento(nombre, presupuesto);
            return ResponseEntity.ok(creado.evaluarPresupuesto());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/presupuesto")
    public ResponseEntity<?> presupuesto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(consultarPresupuestoUseCase.consultarPorDepartamento(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}