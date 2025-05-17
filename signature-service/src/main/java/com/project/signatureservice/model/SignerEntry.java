package com.project.signatureservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "signers")
@Data
@AllArgsConstructor
public class SignerEntry {
    @Id
    private String documentId;
    private List<Signer> signers = new ArrayList<>();
}
