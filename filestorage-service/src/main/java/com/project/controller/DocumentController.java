package com.project.controller;

import com.example.commondto.UploadDocumentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.DocumentMetadata;
import com.project.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadDocumentResponse> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadataJson) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        DocumentMetadata metadata = objectMapper.readValue(metadataJson, DocumentMetadata.class);

        String id = documentService.uploadDocument(file, metadata);
        return ResponseEntity.ok(new UploadDocumentResponse("Document was uploaded successfully", id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable String id) throws IOException {
        return documentService.downloadDocument(id)
                .map(resource -> {
                    try {
                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(resource.getContentType()))
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<DocumentMetadata> getDocumentMetadata(@PathVariable String id) {
        return ResponseEntity.ok(documentService.getMetadata(id));
    }

    @GetMapping("/metadata")
    public ResponseEntity<List<DocumentMetadata>> getDocumentsMetadata(
            @RequestParam(required = false) String uploaderId,
            @RequestParam(required = false) String documentId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return ResponseEntity.ok(documentService.searchDocumentsMetadata(
                uploaderId, documentId, name, tags, fromDate, toDate));
    }

    @PutMapping("/{id}/metadata")
    public ResponseEntity<DocumentMetadata> updateMetadata(
            @PathVariable String id,
            @RequestBody DocumentMetadata updatedMetadata) {
        DocumentMetadata metadata = documentService.updateMetadata(id, updatedMetadata);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/internal/{id}/bytes")
    public ResponseEntity<byte[]> getBytesInternal(@PathVariable String id) {
        return (ResponseEntity<byte[]>) documentService.downloadDocument(id)
                .map(res -> {
                    try {
                        return ResponseEntity.ok(StreamUtils.copyToByteArray(res.getInputStream()));
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/next-id")
    public ResponseEntity<String> generateNextId() {
        return ResponseEntity.ok(new ObjectId().toHexString());
    }

}
