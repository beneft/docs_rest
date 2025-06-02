package com.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignatureVerificationResult {
    private String authorId;
    private String authorName;
    private NcaVerifyResponse verificationResponse;
}
