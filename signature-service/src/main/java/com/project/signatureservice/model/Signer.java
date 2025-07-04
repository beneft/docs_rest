package com.project.signatureservice.model;

import com.example.commondto.SigningStatus;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Signer {
    private String userId;
    private String email;
    private String fullName;
    private String position;
    private String iin;

    private Deputy deputy;

    private SigningStatus status = SigningStatus.PENDING;

    private Integer order = -1;
}
