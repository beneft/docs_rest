package com.project.service;

import com.project.model.RecievedDocuments;
import com.project.repository.RecievedDocumentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class RecievedDocumentsService {
    private final RecievedDocumentsRepository repository;

    public void addDocumentForUser(String userId, String documentId) {
        RecievedDocuments inbox = repository.findByUserId(userId)
                .orElseGet(() -> repository.save(RecievedDocuments.builder()
                        .userId(userId)
                        .documentIds(new ArrayList<>())
                        .build()));
        inbox.getDocumentIds().add(documentId);
        repository.save(inbox);
    }

    public List<String> getDocumentsForUser(String userId) {
        return repository.findByUserId(userId)
                .map(RecievedDocuments::getDocumentIds)
                .orElse(List.of());
    }

    public void removeDocumentForUser(String userId, String documentId) {
        repository.findByUserId(userId).ifPresent(inbox -> {
            inbox.getDocumentIds().remove(documentId);
            repository.save(inbox);
        });
    }
}

