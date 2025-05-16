package com.project.signatureservice.model;

import java.util.List;

public class SigningProcess {
    private String id;
    private String documentId;
    private String authorId;
    private ApprovalType approvalType;

    private List<SignerEntry> signers;

}
