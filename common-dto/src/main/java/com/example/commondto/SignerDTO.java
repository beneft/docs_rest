package com.example.commondto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class SignerDTO {

    private String userId;
    private String fullName;
    private String email;
    private String position;

    private SigningStatus status;

    private boolean canSignNow;
}
