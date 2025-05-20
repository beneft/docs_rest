package com.project.model;

import com.example.commondto.DocumentStatus;
import com.example.commondto.DocumentType;
import com.example.commondto.SignerDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "document_metadata")
public class DocumentMetadata {
    @Id
    private String id;
    private String name;
    private String contentType;
    private LocalDateTime uploadDate;
    private LocalDateTime expirationDate;
    private String uploaderId;
    private List<String> tags;
    private String description;
    private DocumentStatus status;
    private DocumentType type;
    private List<SignerDTO> signers;
}
