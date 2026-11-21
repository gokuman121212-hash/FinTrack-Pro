package com.cafq.fintrack.infrastructure.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RutasRolController {

    @GetMapping("/admin/login")
    public String adminLogin() { return "forward:/login-admin.html"; }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() { return "forward:/admin.html"; }

    @GetMapping("/financiero/login")
    public String financieroLogin() { return "forward:/login-financiero.html"; }

    @GetMapping("/financiero/dashboard")
    public String financieroDashboard() { return "forward:/financiero.html"; }

    @GetMapping("/empleado/login")
    public String empleadoLogin() { return "forward:/login-empleado.html"; }

    @GetMapping("/empleado/dashboard")
    public String empleadoDashboard() { return "forward:/empleado.html"; }
}