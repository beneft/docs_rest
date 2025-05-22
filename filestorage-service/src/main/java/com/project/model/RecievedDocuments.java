package com.project.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "recieved_documents")
public class RecievedDocuments {
    @Id
    private String id;
    private String userId;
    @Builder.Default
    private List<String> documentIds = new ArrayList<>();
}
