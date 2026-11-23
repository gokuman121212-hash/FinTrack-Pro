package com.fintrack.pro.infrastructure.adapter.in.web;

import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.UsuarioSistemaEntity;
import com.fintrack.pro.infrastructure.adapter.out.persistence.springdata.UsuarioSistemaJpaRepository;
import com.fintrack.pro.infrastructure.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UsuarioSistemaJpaRepository usuarioRepo;

    public AuthController(JwtUtil jwtUtil, UsuarioSistemaJpaRepository usuarioRepo) {
        this.jwtUtil = jwtUtil;
        this.usuarioRepo = usuarioRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String usuario = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "").trim();
        String rolEsperado = body.getOrDefault("expectedRole", "").trim().toUpperCase();

        Optional<UsuarioSistemaEntity> opt = usuarioRepo.findByUsernameIgnoreCaseAndActivoTrue(usuario);
        if (opt.isEmpty() || !password.equals(opt.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales invalidas."));
        }

        UsuarioSistemaEntity entidad = opt.get();
        String rolAsignado = entidad.getRol().toUpperCase();

        if (!rolEsperado.isBlank() && !rolEsperado.equals(rolAsignado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Este portal requiere rol " + rolEsperado + ", no " + rolAsignado + "."));
        }

        String token = jwtUtil.generarToken(entidad.getUsername(), rolAsignado);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", rolAsignado,
                "username", entidad.getUsername()
        ));
    }
}