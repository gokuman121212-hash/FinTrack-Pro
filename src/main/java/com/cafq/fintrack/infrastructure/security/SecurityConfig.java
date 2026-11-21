package com.cafq.fintrack.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/admin/login",
                    "/admin/dashboard",
                    "/financiero/login",
                    "/financiero/dashboard",
                    "/empleado/login",
                    "/empleado/dashboard",
                    "/login-admin.html",
                    "/login-financiero.html",
                    "/login-empleado.html",
                    "/admin.html",
                    "/financiero.html",
                    "/empleado.html",
                    "/notas.html",
                    "/app.js",
                    "/api/auth/**",
                    "/api/vistas",
                    "/h2-console/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/departamentos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/departamentos/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/transacciones/**").hasAnyRole("ADMIN", "FINANCIERO", "EMPLEADO")
                .requestMatchers(HttpMethod.GET, "/api/transacciones/**").hasAnyRole("ADMIN", "FINANCIERO", "EMPLEADO")
                .requestMatchers("/api/copilot/**").hasAnyRole("ADMIN", "FINANCIERO")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}