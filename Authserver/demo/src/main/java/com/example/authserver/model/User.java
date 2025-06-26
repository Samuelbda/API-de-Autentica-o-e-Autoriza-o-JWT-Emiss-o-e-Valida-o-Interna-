package com.example.authserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor 
@Data
@Table (name = "users")
@Entity


public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @Column (unique = true, nullable = false)
    private String username;

     @Column(nullable = false)
     private String password;

    @Column(nullable = false) 
      private String role;


}
