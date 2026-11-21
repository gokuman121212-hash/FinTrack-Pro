package com.cafq.fintrack.infrastructure.adapter.in.web;

import com.cafq.fintrack.infrastructure.security.UsuarioCuenta;
import com.cafq.fintrack.infrastructure.security.UsuarioCuentaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/global")
public class AdminGlobalController {

    private final UsuarioCuentaRepository cuentaRepo;
    private final JdbcTemplate jdbc;

    public AdminGlobalController(UsuarioCuentaRepository cuentaRepo, JdbcTemplate jdbc) {
        this.cuentaRepo = cuentaRepo;
        this.jdbc = jdbc;
    }

    @GetMapping("/cuentas")
    public List listarCuentas() {
        List out = new ArrayList();
        for (Object obj : cuentaRepo.findAll()) {
            UsuarioCuenta u = (UsuarioCuenta) obj;
            Map m = new LinkedHashMap();
            m.put("id", u.getId());
            m.put("email", u.getEmail());
            m.put("nombre", u.getNombre());
            m.put("rol", u.getRol());
            m.put("proveedor", u.getProveedor());
            m.put("creadoEn", u.getCreadoEn());
            out.add(m);
        }
        return out;
    }

    @PatchMapping("/cuentas/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable("id") Long id, @RequestBody Map body) {
        return cuentaRepo.findById(id).map(u -> {
            Object rObj = body.get("rol");
            String nuevoRol = (rObj != null && !rObj.toString().isBlank()) ? rObj.toString().trim().toUpperCase() : u.getRol();
            u.setRol(nuevoRol);
            cuentaRepo.save(u);
            Map resp = new LinkedHashMap();
            resp.put("id", u.getId());
            resp.put("rol", u.getRol());
            return ResponseEntity.ok(resp);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cuentas/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable("id") Long id) {
        var opt = cuentaRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UsuarioCuenta target = opt.get();
        String rolEliminado = target.getRol();
        String emailEliminado = target.getEmail();
        cuentaRepo.deleteById(id);
        Map resp = new LinkedHashMap();
        resp.put("eliminado", id);
        resp.put("email", emailEliminado);
        resp.put("rol", rolEliminado);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/departamentos/{id}")
    public ResponseEntity<?> eliminarDepartamento(@PathVariable("id") Long id) {
        int rows = jdbc.update("DELETE FROM departamentos WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.notFound().build();
        }
        Map resp = new LinkedHashMap();
        resp.put("eliminado", id);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/transacciones/{id}")
    public ResponseEntity<?> eliminarTransaccion(@PathVariable("id") Long id) {
        int rows = jdbc.update("DELETE FROM transacciones WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.notFound().build();
        }
        Map resp = new LinkedHashMap();
        resp.put("eliminado", id);
        return ResponseEntity.ok(resp);
    }
}