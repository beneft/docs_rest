package com.project.signatureservice.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignRequest {
    private Long userId;
    private String documentId;
}
