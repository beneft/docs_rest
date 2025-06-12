package com.project.signatureservice.service;

import com.example.commondto.*;
import com.project.signatureservice.client.DocumentFeignClient;
import com.project.signatureservice.client.NotificationClient;
import com.project.signatureservice.kafka.KafkaNotificationProducer;
import com.project.signatureservice.model.*;
import com.project.signatureservice.repository.SignatureRepository;
import com.project.signatureservice.repository.SigningProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final SignatureRepository signatureRepository;
    private final SigningProcessRepository signingProcessRepository;
    private final DocumentFeignClient documentClient;
    private final KafkaNotificationProducer notificationProducer;

    public void startSigningProcess(SigningProcess process) {
        if (process.getApprovalType() == ApprovalType.SEQUENTIAL) {
            process.getSigners().sort(Comparator.comparingInt(Signer::getOrder));
        }
        signingProcessRepository.save(process);
        documentClient.makeSent(process.getDocumentId());
        for (Signer signer: process.getSigners()){
            if (StringUtils.hasText(signer.getUserId()) && !process.getInitiator().equals(signer.getUserId())) {
                documentClient.addReceivedDocument(signer.getUserId(), process.getDocumentId());
            }
        }
        notificationProducer.sendNotification(buildNotificationRequest(process));
    }

    public List<SignerDTO> getSigners(String documentId) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Signing process not found"));
        return process.getSigners().stream()
                .map(s -> {
                    Deputy deputy = s.getDeputy();
                    DeputyDTO deputyDTO = (deputy != null)
                            ? new DeputyDTO(deputy.getId(), deputy.getEmail(), deputy.getName())
                            : null;

                    return new SignerDTO(
                            s.getUserId(),
                            s.getFullName(),
                            s.getEmail(),
                            s.getPosition(),
                            s.getIin(),
                            deputyDTO,
                            s.getStatus(),
                            process.getApprovalType() == ApprovalType.PARALLEL || s.getOrder() == process.getCurrentSignerIndex()
                    );
                })
                .collect(Collectors.toList());
    }

    public boolean canSign(String documentId, String userId) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("No signing process for document: " + documentId));

        if (process.getSigners().stream().anyMatch(s -> s.getStatus() == SigningStatus.DECLINED)) {
            return false;
        }

        return process.getSigners().stream().anyMatch(s -> {
            boolean isMain = false;
            if (s.getUserId()!=null) {
                isMain = s.getUserId().equals(userId);
            } else {
                return false;
            }
            boolean isSubstitute = false;
            if (s.getDeputy()!= null) {
                isSubstitute =  s.getDeputy().getId().equals(userId);
            }
            boolean isPending = s.getStatus() == SigningStatus.PENDING;

            if (!isPending) return false;

            if (process.getApprovalType() == ApprovalType.PARALLEL) {
                return isMain || isSubstitute;
            } else {
                Signer current = process.getSigners().get(process.getCurrentSignerIndex());
                if (s.getDeputy()!=null) {
                    return current.getUserId().equals(userId) || userId.equals(current.getDeputy().getId());
                } else {
                    return current.getUserId().equals(userId);
                }
            }
        });
    }

    public boolean canSignByEmail(String documentId, String email) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("No signing process for document: " + documentId));

        if (process.getSigners().stream().anyMatch(s -> s.getStatus() == SigningStatus.DECLINED)) {
            return false;
        }

        return process.getSigners().stream().anyMatch(s -> {
            boolean isMain = s.getEmail().equals(email);
            boolean isSubstitute = false;
            if (s.getDeputy()!= null) {
                isSubstitute =  s.getDeputy().getEmail().equals(email);
            }
            boolean isPending = s.getStatus() == SigningStatus.PENDING;

            if (!isPending) return false;

            if (process.getApprovalType() == ApprovalType.PARALLEL) {
                return isMain || isSubstitute;
            } else {
                Signer current = process.getSigners().get(process.getCurrentSignerIndex());
                if (s.getDeputy()!=null) {
                    return current.getEmail().equals(email) || email.equals(current.getDeputy().getEmail());
                } else {
                    return current.getEmail().equals(email);
                }
            }
        });
    }

    public void applySignature(Signature signature) {
        String documentId = signature.getDocumentId();
        String userId = signature.getAuthorId();

        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("No signing process for document: " + documentId));


        Signer signer = process.getSigners().stream()
                .filter(s ->
                        (s.getUserId() != null && s.getUserId().equals(userId)) ||
                                (s.getUserId() == null && s.getEmail().equalsIgnoreCase(signature.getAuthorEmail())) ||
                                (s.getDeputy() != null && Objects.equals(userId, "-1") && signature.getAuthorEmail().equals(s.getDeputy().getEmail()))
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Signer not found"));

        if (!Objects.equals(userId, "-1")) {
            if (!canSign(documentId, userId)) {
                throw new IllegalStateException("Signing not allowed for this user at this time");
            }
        } else if (!canSignByEmail(documentId, signature.getAuthorEmail())) {
            throw new IllegalStateException("Signing not allowed for this user at this time");
        }

        signer.setStatus(SigningStatus.SIGNED);
        signature.setSigningDate(LocalDateTime.now());
        signature.setCmsValid(true);
        //signature.setAuthorId(String.valueOf(signer.getUserId()));

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
                .map(s -> {
                    Deputy deputy = s.getDeputy();
                    DeputyDTO deputyDTO = (deputy != null)
                            ? new DeputyDTO(deputy.getId(), deputy.getEmail(), deputy.getName())
                            : null;

                    return new SignerDTO(
                            s.getUserId(),
                            s.getFullName(),
                            s.getEmail(),
                            s.getPosition(),
                            s.getIin(),
                            deputyDTO,
                            s.getStatus(),
                            process.getApprovalType()==ApprovalType.PARALLEL || s.getOrder() == process.getCurrentSignerIndex()
                    );
                })
                .collect(Collectors.toList());
        doc.setSigners(signerDTOs);
        return doc;
    }

    public void declineSignature(String documentId, String userId) {
        SigningProcess process = signingProcessRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Signing process not found"));

        Signer signer = process.getSigners().stream()
                .filter(s -> s.getUserId().equals(userId) ||
                        (s.getDeputy() != null && userId.equals(s.getDeputy().getId())))
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


    private NotificationRequest buildNotificationRequest(SigningProcess process) {
        List<SignerDTO> signerDTOs = process.getSigners().stream()
                //.filter(s -> !Objects.equals(s.getUserId(), process.getInitiator()))
                .map(s -> {
                    boolean canSign = process.getApprovalType() == ApprovalType.PARALLEL ||
                            process.getSigners().indexOf(s) == process.getCurrentSignerIndex();
                    Deputy deputy = s.getDeputy();
                    DeputyDTO deputyDTO = (deputy != null)
                            ? new DeputyDTO(deputy.getId(), deputy.getEmail(), deputy.getName())
                            : null;

                    return new SignerDTO(
                            s.getUserId(),
                            s.getFullName(),
                            s.getEmail(),
                            s.getPosition(),
                            s.getIin(),
                            deputyDTO,
                            s.getStatus(),
                            canSign
                    );
                })
                .collect(Collectors.toList());

        return new NotificationRequest(
                process.getDocumentId(),
                process.getInitiator(),
                "Document " + process.getDocumentId(),
                signerDTOs
        );
    }
}