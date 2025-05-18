package com.project.signatureservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "signature")
public class Signature {
    private String documentId;
    private String authorId;
    private String authorName;
    private String cms;
    private String certificateSerial;
    private String certificateIssuer;
    private LocalDateTime signingDate;
    private LocalDateTime certificateValidFrom;
    private LocalDateTime certificateValidTo;
    private boolean cmsValid;
}
