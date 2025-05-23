package com.project.signatureservice.client;

import com.example.commondto.DocumentMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "document-service")
public interface DocumentFeignClient {

    @GetMapping("/documents/internal/{id}/bytes")
    ResponseEntity<byte[]> getDocumentBytes(@PathVariable("id") String id);

    @PutMapping("/documents/{id}/makesent")
    ResponseEntity<DocumentMetadataDTO> makeSent(@PathVariable("id") String id);

    @GetMapping("/documents/{id}/metadata")
    ResponseEntity<DocumentMetadataDTO> getDocumentMetadata(@PathVariable("id") String id);

    @PutMapping("/documents/{id}/metadata")
    ResponseEntity<DocumentMetadataDTO> updateDocumentMetadata(@PathVariable("id") String id, @RequestBody DocumentMetadataDTO updatedMetadata);

    @GetMapping("/users/{userId}/documents")
    List<String> getReceivedDocuments(@PathVariable("userId") String userId);

    @PostMapping("/users/{userId}/documents")
    void addReceivedDocument(@PathVariable("userId") String userId,
                             @RequestParam("documentId") String documentId);

    @DeleteMapping("/users/{userId}/documents/{documentId}")
    void removeReceivedDocument(@PathVariable("userId") String userId,
                                @PathVariable("documentId") String documentId);
}
