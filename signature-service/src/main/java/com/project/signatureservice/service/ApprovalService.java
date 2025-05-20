package com.project.signatureservice.service;

import com.example.commondto.*;
import com.project.signatureservice.client.DocumentFeignClient;
import com.project.signatureservice.model.*;
import com.project.signatureservice.repository.SignatureRepository;
import com.project.signatureservice.repository.SigningProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final SignatureRepository signatureRepository;
    private final SigningProcessRepository signingProcessRepository;
    private final DocumentFeignClient documentClient;

    public void startSigningProcess(SigningProcess process) {
        if (process.getApprovalType() == ApprovalType.SEQUENTIAL) {
            process.getSigners().sort(Comparator.comparingInt(Signer::getOrder));
        }
        signingProcessRepository.save(process);
    }

    public List<SignerDTO> getSigners(String documentId) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Signing process not found"));
        return process.getSigners().stream()
                .map(s -> new SignerDTO(s.getUserId(), s.getFullName(), s.getEmail(), s.getPosition(), s.getStatus(), false))
                .collect(Collectors.toList());
    }

    public boolean canSign(String documentId, Long userId) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("No signing process for document: " + documentId));

        if (process.getSigners().stream().anyMatch(s -> s.getStatus() == SigningStatus.DECLINED)) {
            return false;
        }

        return process.getSigners().stream().anyMatch(s -> {
            boolean isMain = s.getUserId().equals(userId);
            boolean isSubstitute = s.getSubstituteId() != null && s.getSubstituteId().equals(userId);
            boolean isPending = s.getStatus() == SigningStatus.PENDING;

            if (!isPending) return false;

            if (process.getApprovalType() == ApprovalType.PARALLEL) {
                return isMain || isSubstitute;
            } else {
                Signer current = process.getSigners().get(process.getCurrentSignerIndex());
                return current.getUserId().equals(userId) || userId.equals(current.getSubstituteId());
            }
        });
    }

    public void applySignature(Signature signature) {
        String documentId = signature.getDocumentId();
        Long userId = Long.parseLong(signature.getAuthorId());

        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("No signing process for document: " + documentId));

        Signer signer = process.getSigners().stream()
                .filter(s -> s.getUserId().equals(userId) || userId.equals(s.getSubstituteId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Signer not found"));

        if (!canSign(documentId, userId)) {
            throw new IllegalStateException("Signing not allowed for this user at this time");
        }

        signer.setStatus(SigningStatus.SIGNED);
        signature.setSigningDate(LocalDateTime.now());
        signature.setCmsValid(true);
        signature.setAuthorId(String.valueOf(signer.getUserId()));

        signatureRepository.save(signature);

        if (process.getApprovalType() == ApprovalType.SEQUENTIAL) {
            process.setCurrentSignerIndex(process.getCurrentSignerIndex() + 1);
        }

        signingProcessRepository.save(process);

        boolean allSigned = process.getSigners().stream()
                .allMatch(s -> s.getStatus() == SigningStatus.SIGNED);

        if (allSigned) {
            DocumentMetadataDTO doc = documentClient.getDocumentMetadata(documentId).getBody();
            doc.setType(DocumentType.READY);
            documentClient.updateDocumentMetadata(documentId, doc);
        }
    }

    public DocumentMetadataDTO getDocumentWithSigners(String documentId) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Signing process not found"));

        DocumentMetadataDTO doc = documentClient.getDocumentMetadata(documentId).getBody();
        List<SignerDTO> signerDTOs = process.getSigners().stream()
                .map(s -> new SignerDTO(s.getUserId(), s.getFullName(), s.getEmail(), s.getPosition(), s.getStatus(), false))
                .collect(Collectors.toList());
        doc.setSigners(signerDTOs);
        return doc;
    }

    public void declineSignature(String documentId, Long userId) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Signing process not found"));

        Signer signer = process.getSigners().stream()
                .filter(s -> s.getUserId().equals(userId) || userId.equals(s.getSubstituteId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Signer not found"));

        if (signer.getStatus() != SigningStatus.PENDING) {
            throw new IllegalStateException("User already signed or declined");
        }

        signer.setStatus(SigningStatus.DECLINED);
        signingProcessRepository.save(process);

        DocumentMetadataDTO doc = documentClient.getDocumentMetadata(documentId).getBody();
        doc.setType(DocumentType.REJECTED);
        documentClient.updateDocumentMetadata(documentId, doc);
    }

}