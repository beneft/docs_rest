package com.project.signatureservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Signer {
    private Long userId;
    private SigningStatus status;
    private String email;
}
