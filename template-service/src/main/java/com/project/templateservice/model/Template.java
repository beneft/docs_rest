package com.project.templateservice.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {
    @Id
    private String id;
    private String name;
    private List<Field> fields;
    private String fileId; // GridFS file ID
}