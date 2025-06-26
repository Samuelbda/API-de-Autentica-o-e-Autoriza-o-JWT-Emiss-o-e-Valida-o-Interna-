package com.example.authserver.service;

import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authserver.model.User;
import com.example.authserver.repository.UserRepository;

    
@Service // Indica que esta classe é um serviço Spring
public class AuthService {

    private final UserRepository userRepository; // Injeta o repositório para acesso ao DB
    private final PasswordEncoder passwordEncoder; // Injeta o codificador de senhas (BCryptPasswordEncoder)
    private final JwtService jwtService; // Injeta o serviço de JWT para gerar tokens

    // Construtor: Spring Boot injeta automaticamente as dependências necessárias
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
   
      public String authenticateUserAndGenerateToken(String username, String password) {
        // 1. Buscar o usuário pelo username no banco de dados
        Optional<User> userOptional = userRepository.findByUsername(username);

        // 2. Verificar se o usuário existe
        if (userOptional.isEmpty()) {
            // Se não encontrar o usuário, lança uma exceção de credenciais inválidas
            throw new BadCredentialsException("Credenciais inválidas: Usuário não encontrado.");
        }

        User user = userOptional.get(); // Obtém o objeto User do Optional

        // 3. Verificar se a senha fornecida corresponde à senha codificada no banco de dados
        // `passwordEncoder.matches` compara a senha em texto claro com a senha codificada
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // Se as senhas não corresponderem, lança uma exceção de credenciais inválidas
            throw new BadCredentialsException("Credenciais inválidas: Senha incorreta.");
        }

        // 4. Se tudo estiver correto (usuário encontrado e senha válida), gera e retorna o JWT
        return jwtService.generateToken(user.getUsername(), user.getRole());
    }
}



