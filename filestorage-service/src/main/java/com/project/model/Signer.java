package com.project.model;

import com.project.dto.SigningStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Signer {
    private Long userId;
    private SigningStatus status;
}
