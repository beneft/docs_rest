package com.project.signatureservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "signers")
@Data
@AllArgsConstructor
@Getter
@Setter
public class SigningProcess {
    @Id
    private String documentId;
    private Long initiator;
    private ApprovalType approvalType;
    private List<Signer> signers = new ArrayList<>();
    private int currentSignerIndex = 0;
}
