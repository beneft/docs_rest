package com.project.signatureservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CmsDetailsDTO {
    private String authorName;
    private String cms;
    private String certificateSerial;
    private String certificateIssuer;
    private LocalDateTime signingDate;
    private LocalDateTime certificateValidFrom;
    private LocalDateTime certificateValidTo;
    private boolean cmsValid;
}
