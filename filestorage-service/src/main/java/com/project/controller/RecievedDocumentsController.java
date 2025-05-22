package com.project.controller;

import com.project.service.RecievedDocumentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/documents")
public class RecievedDocumentsController {
    private final RecievedDocumentsService service;

    @GetMapping
    public ResponseEntity<List<String>> getDocuments(@PathVariable String userId) {
        return ResponseEntity.ok(service.getDocumentsForUser(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addDocument(@PathVariable String userId, @RequestParam String documentId) {
        service.addDocumentForUser(userId, documentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> removeDocument(@PathVariable String userId, @PathVariable String documentId) {
        service.removeDocumentForUser(userId, documentId);
        return ResponseEntity.ok().build();
    }
}
