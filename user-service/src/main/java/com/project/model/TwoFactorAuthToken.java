package com.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorAuthToken {
    @Id
    private String token;
    private String userId;
    private Instant expiresAt;

    private String password;
}
