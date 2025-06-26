package com.example.authserver.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api")
@Tag(name = "Recursos Protegidos", description = "Endpoints que exigem autenticação JWT") // Tag no Swagger UI
@SecurityRequirement(name = "bearerAuth")

public class TestProtectedController {
     @Operation(summary = "Endpoint acessível por qualquer usuário autenticado")
    @GetMapping("/hello")
    public String hello() {
        return "Olá! Você acessou um endpoint protegido com sucesso!";
    }
      @Operation(summary = "Endpoint acessível apenas por usuários com a role 'ADMIN'")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Exige que o JWT do usuário tenha a role 'ADMIN' para acessar
    public String adminOnly() {
        return "Bem-vindo, Administrador! Este é um recurso restrito.";
    }
      // NOVO ENDPOINT: para demonstrar o username do token
    @Operation(summary = "Endpoint que retorna o username do usuário logado (extraído do token)")
    @GetMapping("/me")
    public String me(java.security.Principal principal) {
        // 'Principal' é uma interface do Java Security que representa o usuário autenticado.
        // O Spring Security preenche isso automaticamente com base no seu token JWT.
        return "Você está logado como: " + principal.getName();
    }
}




