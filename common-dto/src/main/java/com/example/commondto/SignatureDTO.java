package com.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignatureDTO {
    private String documentId;
    private String authorId;
    private String authorName;
    private String authorEmail;
    private String authorOrganization;
    private LocalDateTime signingDate;
    private boolean cmsValid;
}
