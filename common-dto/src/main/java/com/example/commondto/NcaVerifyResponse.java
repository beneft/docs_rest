package com.example.commondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NcaVerifyResponse {
    private int status;
    private String message;
    private boolean valid;

    private List<SignerEntry> signers;

    @Data
    public static class SignerEntry {
        private List<CertificateInfo> certificates;
        private TspInfo tsp;
    }

    @Data
    public static class CertificateInfo {
        private boolean valid;
        private List<RevocationInfo> revocations;
        private String notBefore;
        private String notAfter;
        private String keyUsage;
        private String serialNumber;
        private String signAlg;
        private List<String> keyUser;
        private String publicKey;
        private String signature;
        private Subject subject;
        private Issuer issuer;
    }

    @Data
    public static class RevocationInfo {
        private boolean revoked;
        private String by;
        private String revocationTime;
        private String reason;
    }

    @Data
    public static class Subject {
        private String commonName;
        private String surName;
        private String iin;
        private String country;
        private String dn;
    }

    @Data
    public static class Issuer {
        private String commonName;
        private String country;
        private String dn;
    }

    @Data
    public static class TspInfo {
        private String serialNumber;
        private String genTime;
        private String policy;
        private String tsa;
        private String tspHashAlgorithm;
        private String hash;
    }
}
