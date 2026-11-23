package com.fintrack.pro.infrastructure.adapter.out.persistence.springdata;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_agente")
public class AuditoriaAgenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String pregunta;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String respuesta;

    @Column(name = "pasos_usados", nullable = false)
    private int pasosUsados;

    @Column(name = "latencia_ms", nullable = false)
    private long latenciaMs;

    @Column(name = "consultado_en", nullable = false)
    private LocalDateTime consultadoEn = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public int getPasosUsados() { return pasosUsados; }
    public void setPasosUsados(int pasosUsados) { this.pasosUsados = pasosUsados; }
    public long getLatenciaMs() { return latenciaMs; }
    public void setLatenciaMs(long latenciaMs) { this.latenciaMs = latenciaMs; }
    public LocalDateTime getConsultadoEn() { return consultadoEn; }
    public void setConsultadoEn(LocalDateTime consultadoEn) { this.consultadoEn = consultadoEn; }
}