package com.project.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class UploadDocumentResponse {
    private String message;
    private String documentId;
}
