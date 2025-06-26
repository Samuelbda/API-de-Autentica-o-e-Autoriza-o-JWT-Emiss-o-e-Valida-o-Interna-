package com.example.authserver.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.authserver.model.User;
import com.example.authserver.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@SuppressWarnings("unused")
@Configuration
@EnableWebSecurity 

public class SecurityConfig {
    
     @Value("${jwt.secret}")
    private String jwtSecret;
     
     @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username) // Tenta encontrar o usuário pelo username
                .map(user -> org.springframework.security.core.userdetails.User.builder() // Se encontrar, constrói um User do Spring Security
                        .username(user.getUsername())
                        .password(user.getPassword()) // A senha já está codificada no DB
                        .roles(user.getRole()) // Define a(s) role(s) do usuário
                        .build())
                // Se o usuário não for encontrado, lança uma exceção
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }


     @Bean
    public JwtDecoder jwtDecoder() {
        // A chave secreta do application.yml é usada para criar uma chave simétrica para HMAC SHA256
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSha256");
        // Retorna uma instância de NimbusJwtDecoder que fará a validação da assinatura do JWT
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
    // Bean para SecurityFilterChain: Configura as regras de segurança HTTP da aplicação
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF (Cross-Site Request Forgery) para APIs RESTful stateless (sem estado de sessão)
            .csrf(AbstractHttpConfigurer::disable)
            // Configura a política de gerenciamento de sessão como STATELESS, o que é crucial para JWT
            // Isso significa que a API não manterá sessões de usuário no servidor. Cada requisição é independente.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configura as regras de autorização para as requisições HTTP
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login").permitAll() // Permite acesso público ao endpoint de login
                .requestMatchers("/auth/validate").permitAll() // Permite acesso público ao endpoint de validação de token
                .requestMatchers("/h2-console/**").permitAll() // Permite acesso público ao console do H2 Database
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Permite acesso público à documentação Swagger/OpenAPI
                .anyRequest().authenticated() // Qualquer outra requisição exige que o usuário esteja autenticado com um JWT válido
            )
            // Configura cabeçalhos para permitir que o H2 console funcione dentro de um frame (iframe)
            .headers(headers -> headers.frameOptions(frameOptions -> headers.frameOptions().sameOrigin()))
            // Configura o servidor de recursos OAuth2 para usar o decodificador JWT que definimos
            // Isso faz com que o Spring Security intercepte requisições com "Authorization: Bearer <token>"
            // e valide o token usando o JwtDecoder configurado.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                // Ao chamar .jwt(), o Spring Security usará o JwtDecoder que definimos como um Bean.
            }));

        // Constrói e retorna a cadeia de filtros de segurança configurada
        return http.build();
    }


    // CommandLineRunner: Um bean que é executado uma vez quando a aplicação Spring Boot inicia.
    // Usado aqui para popular o banco de dados H2 com usuários iniciais (admin e user) se eles não existirem.
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica se o usuário 'admin' já existe. Se não, cria.
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User(null, "admin", passwordEncoder.encode("123456"), "ADMIN");
                userRepository.save(admin);
                System.out.println("✅ Usuário 'admin' criado com senha codificada.");
            }
            // Verifica se o usuário 'user' já existe. Se não, cria.
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User(null, "user", passwordEncoder.encode("password"), "USER");
                userRepository.save(user);
                System.out.println("✅ Usuário 'user' criado com senha codificada.");
            }
        };
    }



}
