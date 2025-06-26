package com.example.authserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authserver.service.AuthService;
import com.example.authserver.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController 
@RequestMapping("/auth") 
@Tag(name = "Autenticação", description = "Endpoints para login e geração/validação de tokens JWT") // Tag no Swagger UI

public class AuthController {


    private final AuthService authService;
    private final JwtService jwtService;

      public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

      @Operation(summary = "Realiza o login do usuário e emite um token JWT") // Descrição para o Swagger
    @ApiResponses(value = { // Descreve as possíveis respostas HTTP para este endpoint
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido, retorna o token JWT"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
     @PostMapping("/login") // Mapeia requisições POST para /auth/login
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String Stringpassword) {
        try {
            // Tenta autenticar o usuário e gerar um token usando o AuthService
            String token = authService.authenticateUserAndGenerateToken(username, Stringpassword);
            return ResponseEntity.ok(token); // Se sucesso, retorna o token com status 200 OK
        } catch (BadCredentialsException e) {
            // Se as credenciais forem inválidas (username ou password), retorna 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // Captura qualquer outro erro inesperado e retorna 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno ao tentar logar.");
        }
    }
    @Operation(summary = "Valida um token JWT (útil para debug e verificação externa)") // Descrição para o Swagger
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @PostMapping("/validate") // Mapeia requisições POST para /auth/validate
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        // Usa o JwtService para verificar se o token é válido
        if (jwtService.validateToken(token)) {
            // Se válido, retorna 200 OK e o username extraído do token (para demonstração)
            return ResponseEntity.ok("Token válido! Username: " + jwtService.getUsernameFromToken(token));
        } else {
            // Se inválido (expirado, assinatura incorreta, etc.), retorna 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
        }
    }
}


