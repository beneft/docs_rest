package com.example.commondto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class NotificationRequest {
    private String documentId;
    private String initiator;
    private String documentName;
    private List<SignerDTO> signers;
}
