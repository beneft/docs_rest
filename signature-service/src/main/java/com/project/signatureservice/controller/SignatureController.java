package com.project.signatureservice.controller;

import com.project.signatureservice.model.Signature;
import com.project.signatureservice.service.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/signatures")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
@RequiredArgsConstructor
public class SignatureController {

    public ResponseEntity<Void> verifySignatures(){return null;};

    public ResponseEntity<Void> startDocumentApproval(){return null;}


    private final SignatureService signatureService;

    @GetMapping
    public ResponseEntity<List<Signature>> getAllSignatures() {
        return ResponseEntity.ok(signatureService.getAllSignatures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Signature> getSignatureById(@PathVariable String id) {
        return signatureService.getSignatureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<Signature>> getSignaturesByDocumentId(@PathVariable String documentId) {
        return ResponseEntity.ok(signatureService.getSignaturesByDocumentId(documentId));
    }

    @PostMapping
    public ResponseEntity<Signature> createSignature(@RequestBody Signature signature) {
        return ResponseEntity.ok(signatureService.saveSignature(signature));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSignature(@PathVariable String id) {
        signatureService.deleteSignature(id);
        return ResponseEntity.noContent().build();
    }



}
