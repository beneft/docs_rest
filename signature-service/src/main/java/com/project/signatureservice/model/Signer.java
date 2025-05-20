package com.project.signatureservice.model;

import com.example.commondto.SigningStatus;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Signer {
    private Long userId;
    private String email;
    private String fullName;
    private String position;

    private Long substituteId;
    private String substituteEmail;
    private String substituteName;

    private SigningStatus status = SigningStatus.PENDING;

    private Integer order = 0;
}
