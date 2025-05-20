package com.project.signatureservice.model;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "signature")
public class Signature {
    private String documentId;
    @NotBlank
    private String authorId;
    @NotBlank
    private String authorName;
    private String authorOrganization;
    @NotBlank
    private String cms;
    private String certificateSerial;
    private String certificateIssuer;
    private LocalDateTime signingDate;
    private LocalDateTime certificateValidFrom;
    private LocalDateTime certificateValidTo;
    private boolean cmsValid;
}
