package com.example.authserver.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@SuppressWarnings("unused")
@Service
public class JwtService {
    
    @Value ("${jwt.secret}")
    private String secretKey;



    @Value ("${jwt.expiration}")
    private Long expirationTime;


    public String generateToken(String username, String role) {
        return JWT.create() // Inicia a criação do JWT
                .withSubject(username) // Define o "assunto" do token (quem é o dono)
                .withClaim("role", role) // Adiciona uma informação personalizada (a role do usuário)
                .withIssuedAt(new Date()) // Define a data de emissão do token (agora)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime)) // Define a data de expiração
                .sign(Algorithm.HMAC256(secretKey)); // Assina o token com HMAC256 e sua chave secreta
    }

     public boolean validateToken(String token) {
        try {
            // Cria um objeto verificador usando o mesmo algoritmo e chave secreta usados para criar o token
            JWT.require(Algorithm.HMAC256(secretKey))
               .build() // Constrói o verificador
               .verify(token); // Tenta verificar o token. Se falhar, lança JWTVerificationException.
            return true; // Se não lançar exceção, o token é válido
        } catch (JWTVerificationException e) {
            System.err.println("Erro na validação do token: " + e.getMessage()); // Exibe o erro no console
            return false;
        }
    }

     

     public String getUsernameFromToken(String token) {
        // Decodifica o token para ler suas claims. Isso não verifica a assinatura.
        return JWT.decode(token).getSubject();
    }

      public Map<String, Object> getAllClaimsFromToken(String token) {
        return JWT.decode(token).getClaims().entrySet().stream()
                   .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue().asString()), HashMap::putAll);
    }
}

