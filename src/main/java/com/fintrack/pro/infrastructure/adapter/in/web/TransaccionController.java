package com.fintrack.pro.infrastructure.adapter.in.web;

import com.fintrack.pro.domain.model.Transaccion;
import com.fintrack.pro.domain.port.in.RegistrarTransaccionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final RegistrarTransaccionUseCase registrarTransaccionUseCase;

    public TransaccionController(RegistrarTransaccionUseCase registrarTransaccionUseCase) {
        this.registrarTransaccionUseCase = registrarTransaccionUseCase;
    }

    @GetMapping
    public List<Transaccion> listar() {
        return registrarTransaccionUseCase.listar();
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
        try {
            Long departamentoId = Long.valueOf(String.valueOf(body.get("departamentoId")));
            String tipo = String.valueOf(body.get("tipo"));
            BigDecimal monto = new BigDecimal(String.valueOf(body.get("monto")));
            String categoria = String.valueOf(body.getOrDefault("categoria", "General"));
            String descripcion = String.valueOf(body.getOrDefault("descripcion", ""));
            Transaccion tx = registrarTransaccionUseCase.registrar(departamentoId, tipo, monto, categoria, descripcion);
            return ResponseEntity.ok(tx);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}