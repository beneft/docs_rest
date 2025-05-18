package com.project.signatureservice.service;

import com.project.signatureservice.model.Signature;
import com.project.signatureservice.repository.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRepository signatureRepository;

    public List<Signature> getAllSignatures() {
        return signatureRepository.findAll();
    }

    public Optional<Signature> getSignatureById(String id) {
        return signatureRepository.findById(id);
    }

    public List<Signature> getSignaturesByDocumentId(String documentId) {
        return signatureRepository.findByDocumentId(documentId);
    }

    public Signature saveSignature(Signature signature) {
        return signatureRepository.save(signature);
    }

    public void deleteSignature(String id) {
        signatureRepository.deleteById(id);
    }
}
