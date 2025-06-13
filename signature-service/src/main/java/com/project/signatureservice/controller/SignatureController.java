package com.project.signatureservice.controller;

import com.example.commondto.CmsDetailsDTO;
import com.example.commondto.SignatureDTO;
import com.example.commondto.SignatureVerificationResult;
import com.project.signatureservice.model.Signature;
import com.project.signatureservice.service.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/signatures")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
@RequiredArgsConstructor
public class SignatureController {


    @PostMapping("/verify")
    public ResponseEntity<CmsDetailsDTO> verifySignatures(
            @RequestParam("file") MultipartFile uploadedFile,
            @RequestParam("id") String documentId) {
        return ResponseEntity.ok(signatureService.verifySignatures(uploadedFile,documentId));
    }

    @PostMapping("/verify/v2")
    public ResponseEntity<List<SignatureVerificationResult>> verify(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") String documentId
    ) {
        return ResponseEntity.ok(signatureService.verifySignaturesV2(file, documentId));
    }

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
    public ResponseEntity<List<SignatureDTO>> getSignaturesByDocumentId(@PathVariable String documentId) {
        return ResponseEntity.ok(signatureService.getSignaturesByDocumentId(documentId));
    }

    //////////////////////////// FOR REMOVAL /////////////////////////////////////////
//    @PostMapping
//    public ResponseEntity<Signature> createSignature(@RequestBody Signature signature) {
//        return ResponseEntity.ok(signatureService.saveSignature(signature));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteSignature(@PathVariable String id) {
//        signatureService.deleteSignature(id);
//        return ResponseEntity.noContent().build();
//    }
}
