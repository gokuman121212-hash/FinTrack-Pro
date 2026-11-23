package com.cafq.fintrack.infrastructure.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "usuarios_cuenta")
public class UsuarioCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String rol;

    @Column(nullable = false)
    private String proveedor;

    private String providerSub;

    @Column(nullable = false)
    private Instant creadoEn = Instant.now();

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getProviderSub() { return providerSub; }
    public void setProviderSub(String providerSub) { this.providerSub = providerSub; }
    public Instant getCreadoEn() { return creadoEn; }
}