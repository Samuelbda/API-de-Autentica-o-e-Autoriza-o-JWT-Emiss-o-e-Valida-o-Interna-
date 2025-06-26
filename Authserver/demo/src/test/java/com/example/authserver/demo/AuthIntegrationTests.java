package com.example.authserver.demo; // <-- CORREÇÃO: Pacote agora é 'demo' para alinhar com DemoApplication

import com.example.authserver.demo.DemoApplication; // <-- NOVO IMPORT: Para referenciar a classe principal da aplicação
import com.example.authserver.repository.UserRepository; // <-- Import correto: 'repository' minúsculo
import com.example.authserver.service.AuthService; 
import com.example.authserver.service.JwtService; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

import static org.assertj.core.api.Assertions.assertThat; // <-- NOVO IMPORT: Para usar o assertThat

// CORREÇÃO: Adicionando 'classes = DemoApplication.class' para garantir que o contexto seja carregado corretamente
@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService; // Injetado para popular usuários, mas não para testar a lógica do serviço diretamente
    
    @Autowired
    private JwtService jwtService; // Injetado para validar tokens gerados nos testes

    @Autowired
    private UserRepository userRepository; // Injetado para popular usuários

    @Autowired
    private PasswordEncoder passwordEncoder; // Injetado para codificar senhas ao popular usuários

    @BeforeEach
    void setup() {
        // Garante que o banco de dados H2 em memória tenha os usuários para os testes
        // Ele verifica se o usuário 'admin' já existe. Se não, cria.
        userRepository.findByUsername("admin").ifPresentOrElse(
                user -> { /* Usuário 'admin' já existe, não faz nada */ },
                () -> {
                    // Cria uma nova instância de User com a senha codificada
                    com.example.authserver.model.User admin = new com.example.authserver.model.User(null, "admin", passwordEncoder.encode("123456"), "ADMIN");
                    userRepository.save(admin); // Salva o usuário no repositório
                    System.out.println("DEBUG: Usuário 'admin' garantido no H2 para testes.");
                }
        );
        // Verifica se o usuário 'user' já existe. Se não, cria.
        userRepository.findByUsername("user").ifPresentOrElse(
                user -> { /* Usuário 'user' já existe, não faz nada */ },
                () -> {
                    // Cria uma nova instância de User com a senha codificada
                    com.example.authserver.model.User regularUser = new com.example.authserver.model.User(null, "user", passwordEncoder.encode("password"), "USER");
                    userRepository.save(regularUser); // Salva o usuário no repositório
                    System.out.println("DEBUG: Usuário 'user' garantido no H2 para testes.");
                }
        );
    }

    @Test
    void testLoginSuccess() throws Exception {
        String token = mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString(); // Obtém o corpo da resposta (o token)

        // Valida que o token não é nulo/vazio e que o JwtService pode validá-lo
        assertThat(token).isNotEmpty();
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    void testLoginFailureInvalidPassword() throws Exception {
        mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "senhaErrada") // Senha incorreta
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isUnauthorized()) // Espera status 401
               .andExpect(content().string(containsString("Senha incorreta."))); // Espera a mensagem de erro específica
    }

    @Test
    void testProtectedEndpointAccessDeniedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/hello"))
               .andExpect(status().isUnauthorized()); // Espera status 401 (sem token)
    }

    @Test
    void testProtectedEndpointAccessWithValidToken() throws Exception {
        // Primeiro, faz login para obter um token válido de um usuário comum
        String token = mockMvc.perform(post("/auth/login")
                .param("username", "user")
                .param("password", "password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        // Tenta acessar o endpoint protegido com o token válido
        mockMvc.perform(get("/api/hello")
                .header("Authorization", "Bearer " + token)) // Adiciona o token no cabeçalho Authorization
               .andExpect(status().isOk()) // Espera status 200
               .andExpect(content().string("Olá! Você acessou um endpoint protegido com sucesso!"));
    }

    @Test
    void testProtectedAdminEndpointAccessWithAdminToken() throws Exception {
        // Primeiro, faz login para obter um token válido de um administrador
        String adminToken = mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        // Tenta acessar o endpoint de admin com o token de admin
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + adminToken)) // Adiciona o token no cabeçalho Authorization
               .andExpect(status().isOk()) // Espera status 200
               .andExpect(content().string("Bem-vindo, Administrador! Este é um recurso restrito."));
    }

    @Test
    void testProtectedAdminEndpointAccessDeniedWithUserToken() throws Exception {
        // Primeiro, faz login para obter um token válido de um usuário comum
        String userToken = mockMvc.perform(post("/auth/login")
                .param("username", "user")
                .param("password", "password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        // Tenta acessar o endpoint de admin com o token de um usuário comum
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + userToken)) // Adiciona o token no cabeçalho Authorization
               .andExpect(status().isForbidden()); // Espera status 403 (Forbidden - acesso negado por falta de role)
    }
}