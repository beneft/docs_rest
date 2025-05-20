package com.example.commondto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadataDTO {
    private String id;
    private String name;
    private String contentType;
    private LocalDateTime uploadDate;
    private LocalDateTime expirationDate;
    private String uploaderId;
    private List<String> tags;
    private String description;

}
