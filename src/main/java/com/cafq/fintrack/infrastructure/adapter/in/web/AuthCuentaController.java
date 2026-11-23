package com.cafq.fintrack.infrastructure.adapter.in.web;

import com.cafq.fintrack.infrastructure.security.UsuarioCuenta;
import com.cafq.fintrack.infrastructure.security.UsuarioCuentaRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/cuenta")
public class AuthCuentaController {

    private final UsuarioCuentaRepository repo;
    private final PasswordEncoder encoder;
    private final SecretKey signingKey;

    public AuthCuentaController(
            UsuarioCuentaRepository repo,
            PasswordEncoder encoder,
            @Value("${app.jwt.secret:FinTrackProSecretKey2026SuperSeguraParaFirmarTokensJWT123456}") String secret
    ) {
        this.repo = repo;
        this.encoder = encoder;
        String padded = secret.length() >= 32 ? secret : (secret + "0123456789ABCDEF0123456789ABCDEF");
        this.signingKey = Keys.hmacShaKeyFor(padded.substring(0, Math.max(32, padded.length())).getBytes(StandardCharsets.UTF_8));
    }

    private String normalizarRol(String rol) {
        if (rol == null) return "EMPLEADO";
        String r = rol.replace("ROLE_", "").trim().toUpperCase(Locale.ROOT);
        return switch (r) {
            case "ADMIN", "ADMINISTRADOR" -> "ADMIN";
            case "FINANCIERO", "FINANZAS" -> "FINANCIERO";
            default -> "EMPLEADO";
        };
    }

    private String emitirToken(UsuarioCuenta u) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(u.getEmail())
                .claim("role", u.getRol())
                .claim("roles", u.getRol())
                .claim("nombre", u.getNombre())
                .claim("provider", u.getProveedor())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(24, ChronoUnit.HOURS)))
                .signWith(signingKey)
                .compact();
    }

    private Map<String, Object> respuestaAuth(UsuarioCuenta u) {
        String token = emitirToken(u);
        return Map.of(
                "token", token,
                "accessToken", token,
                "id", u.getId(),
                "email", u.getEmail(),
                "nombre", u.getNombre(),
                "rol", u.getRol(),
                "proveedor", u.getProveedor()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim().toLowerCase(Locale.ROOT);
        String password = body.getOrDefault("password", "");
        String nombre = body.getOrDefault("nombre", "").trim();
        String rol = normalizarRol(body.getOrDefault("rol", "EMPLEADO"));

        if (email.isBlank() || password.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ingresa un correo valido y una contrasena de al menos 4 caracteres."));
        }
        if (repo.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Ya existe una cuenta registrada con ese correo."));
        }
        if (nombre.isBlank()) {
            nombre = email.split("@")[0];
        }

        UsuarioCuenta u = new UsuarioCuenta();
        u.setEmail(email);
        u.setNombre(nombre);
        u.setPasswordHash(encoder.encode(password));
        u.setRol(rol);
        u.setProveedor("LOCAL");
        repo.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaAuth(u));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim().toLowerCase(Locale.ROOT);
        String password = body.getOrDefault("password", "");
        String rolEsperado = body.containsKey("rol") && body.get("rol") != null && !body.get("rol").isBlank()
                ? normalizarRol(body.get("rol")) : null;

        var opt = repo.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No existe una cuenta con ese correo. Registrate primero."));
        }
        UsuarioCuenta u = opt.get();
        if (!encoder.matches(password, u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Contrasena incorrecta."));
        }
        if (rolEsperado != null && !rolEsperado.equals(u.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Esta cuenta tiene rol " + u.getRol() + ", no " + rolEsperado + "."));
        }
        return ResponseEntity.ok(respuestaAuth(u));
    }
}