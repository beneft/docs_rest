package com.project.signatureservice.service;

import com.example.commondto.CmsDetailsDTO;
import com.example.commondto.DocumentBytesResponse;
import com.example.commondto.DocumentMetadataDTO;
import com.example.commondto.SignatureDTO;
import com.project.signatureservice.client.DocumentFeignClient;
import com.project.signatureservice.kafka.DocumentBytesKafkaClient;
import com.project.signatureservice.model.Signature;
import com.project.signatureservice.repository.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRepository signatureRepository;
    @Autowired
    private DocumentFeignClient documentFeignClient;
    @Autowired
    private DocumentBytesKafkaClient kafkaClient;

    public List<Signature> getAllSignatures() {
        return signatureRepository.findAll();
    }

    public Optional<Signature> getSignatureById(String id) {
        return signatureRepository.findById(id);
    }

    public CmsDetailsDTO verifySignatures(MultipartFile uploadedFile, String documentId){
        CmsDetailsDTO cmsDetails = new CmsDetailsDTO();
        try {
            ResponseEntity<byte[]> response = documentFeignClient.getDocumentBytes(documentId);
            if (!response.getStatusCode().is2xxSuccessful()) {
                cmsDetails.setCmsValid(false);
                return cmsDetails;
            }

            byte[] originalBytes = response.getBody();
            byte[] uploadedBytes = uploadedFile.getBytes();

            if (!Arrays.equals(originalBytes, uploadedBytes)) {
                cmsDetails.setCmsValid(false);
                return cmsDetails;
            }

            List<SignatureDTO> signatures = getSignaturesByDocumentId(documentId);
            DocumentMetadataDTO metadata = documentFeignClient.getDocumentMetadata(documentId).getBody();

            cmsDetails.setCmsValid(true);
            cmsDetails.setSignatures(signatures);
            cmsDetails.setUploaderId(metadata.getUploaderId());
            cmsDetails.setCreatedDate(metadata.getUploadDate());

            return cmsDetails;
        } catch (IOException e) {
            cmsDetails.setCmsValid(false);
            return cmsDetails;
        }
    }

    public List<SignatureDTO> getSignaturesByDocumentId(String documentId) {
        return signatureRepository.findByDocumentId(documentId).stream()
                .map(sig -> new SignatureDTO(
                        sig.getDocumentId(),
                        sig.getAuthorId(),
                        sig.getAuthorName(),
                        sig.getAuthorOrganization(),
                        sig.getSigningDate(),
                        sig.isCmsValid()
                ))
                .toList();
    }

    public Signature saveSignature(Signature signature) {
        signature.setSigningDate(LocalDateTime.now());
        signature.setCmsValid(true);
        return signatureRepository.save(signature);
    }

    public void deleteSignature(String id) {
        signatureRepository.deleteById(id);
    }
}
