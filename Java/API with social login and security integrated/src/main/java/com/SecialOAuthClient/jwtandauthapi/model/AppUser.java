package com.eventaccess.jwtandauthapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id @GeneratedValue(strategy= GenerationType.UUID)
    private String id;
    private String username;
    private String password;
    private String role;
}
