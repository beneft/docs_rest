package com.project.signatureservice.controller;

import com.example.commondto.DocumentMetadataDTO;
import com.example.commondto.SignerDTO;
import com.project.signatureservice.model.Signature;
import com.project.signatureservice.model.SigningProcess;
import com.project.signatureservice.service.ApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/approval")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;
    @PostMapping("/start") //authorize
    public ResponseEntity<String> startSigningProcess(@RequestBody SigningProcess signingProcess) {
        approvalService.startSigningProcess(signingProcess);
        return ResponseEntity.ok("Signing process started.");
    }

    @GetMapping("/{documentId}/signers")
    public ResponseEntity<List<SignerDTO>> getSigners(@PathVariable String documentId) {
        return ResponseEntity.ok(approvalService.getSigners(documentId));
    }

    @GetMapping("/{documentId}/can-sign/{userId}")
    public ResponseEntity<Boolean> canSign(
            @PathVariable String documentId,
            @PathVariable String userId) {
        return ResponseEntity.ok(approvalService.canSign(documentId, userId));
    }

    @PostMapping("/sign")
    public ResponseEntity<String> sign(@Valid @RequestBody Signature signature) {
        approvalService.applySignature(signature);
        return ResponseEntity.ok("Signature recorded.");
    }

    @GetMapping("/{documentId}/status")
    public ResponseEntity<DocumentMetadataDTO> getDocumentStatus(@PathVariable String documentId) {
        return ResponseEntity.ok(approvalService.getDocumentWithSigners(documentId));
    }

    @PostMapping("/{documentId}/decline/{userId}")
    public ResponseEntity<String> declineSignature(@PathVariable String documentId, @PathVariable String userId) {
        approvalService.declineSignature(documentId, userId);
        return ResponseEntity.ok("Signature declined.");
    }

}
