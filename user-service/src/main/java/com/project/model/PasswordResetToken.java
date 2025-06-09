package com.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetToken {
    @Id
    private String token;

    private String userId;
    private Instant expiresAt;
}
