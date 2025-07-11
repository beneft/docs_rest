package com.project.controller;

import com.example.commondto.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadDocumentResponse> uploadDocument(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadataJson) throws IOException {
        DocumentMetadata metadata =
                new ObjectMapper().readValue(metadataJson, DocumentMetadata.class);
        metadata.setUploaderId(jwt.getSubject());
        String id = documentService.uploadDocument(file, metadata);
        return ResponseEntity.ok(new UploadDocumentResponse("Document was uploaded successfully", id));
    }


    @PostMapping("/template")
    public ResponseEntity<UploadDocumentResponse> uploadTemplateDocumentRaw(
            @RequestHeader("X-File-Name") String name,
            @RequestHeader("X-Content-Type") String contentType,
            @RequestHeader("X-Uploader-Id") String uploaderId,
            @RequestBody byte[] data) throws IOException {

        DocumentMetadata metadata = DocumentMetadata.builder()
                .name(name+"-id"+generateNextId().getBody()+".docx")
                .contentType(contentType)
                .uploaderId(uploaderId)
                .uploadDate(LocalDateTime.now())
                .status(DocumentStatus.ACTIVE)
                .type(DocumentType.DRAFT)
                .build();

        String id = documentService.uploadDocumentRaw(data, metadata);
        return ResponseEntity.ok(new UploadDocumentResponse("Template uploaded successfully", id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable String id, @RequestParam(value = "download", required = false, defaultValue = "false") boolean forceDownload) throws IOException {
        return documentService.downloadDocument(id)
                .map(resource -> {
                    try {
                        String filename = resource.getFilename();
                        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
                        String dispositionType = forceDownload ? "attachment" : "inline";
                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(resource.getContentType()))
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                        dispositionType + "; filename*=UTF-8''" + encodedFilename)
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("@sec.isAuthor(#id, #jwt.subject, @documentMetadataRepository)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id,
                                               @AuthenticationPrincipal Jwt jwt) {
        documentService.deleteDocument(id);            // здесь уже безопасно
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/metadata")
    public ResponseEntity<DocumentMetadata> getDocumentMetadata(@PathVariable String id) {
        return ResponseEntity.ok(documentService.getMetadata(id));
    }

    @GetMapping("/metadata")
    public ResponseEntity<List<DocumentMetadataDTO>> getDocumentsMetadata(
            @RequestParam(required = false) String uploaderId,
            @RequestParam(required = false) String documentId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) DocumentType type
    ) {
        return ResponseEntity.ok(documentService.searchDocumentsMetadata(
                uploaderId, documentId, name, tags, fromDate, toDate, type));
    }

    @PutMapping("/{id}/metadata")
    public ResponseEntity<DocumentMetadataDTO> updateMetadata(
            @PathVariable String id,
            @RequestBody DocumentMetadataDTO updatedMetadata) {
        DocumentMetadataDTO metadata = documentService.updateMetadata(id, updatedMetadata);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/{id}/metadata/signers")
    public ResponseEntity<List<SignerListDTO>> getMetadataSigners(
            @PathVariable String id) {
        List<SignerListDTO> signers = documentService.getMetadataSigners(id);
        return ResponseEntity.ok(signers);
    }

    @PutMapping("/{id}/metadata/signers")
    public ResponseEntity<DocumentMetadataDTO> updateMetadataSigners(
            @PathVariable String id,
            @RequestBody List<SignerListDTO> updatedSigners) {
        DocumentMetadataDTO metadata = documentService.updateMetadataSigners(id, updatedSigners);
        return ResponseEntity.ok(metadata);
    }

    @PutMapping("/{id}/makesent")
    public ResponseEntity<DocumentMetadataDTO> makeSent(
            @PathVariable String id) {
        DocumentMetadataDTO metadata = documentService.makeSent(id);
        return ResponseEntity.ok(metadata);
    }

    @PutMapping("/{id}/metadata/expiration")
    public ResponseEntity<DocumentMetadataDTO> setExpirationDate(
            @PathVariable String id, @RequestBody LocalDateTime expirationDate) {
        DocumentMetadataDTO metadata = documentService.setExpirationDate(id, expirationDate);
        return ResponseEntity.ok(metadata);
    }

    @PutMapping("/{id}/metadata/tag")
    public ResponseEntity<DocumentMetadataDTO> toggleTag(
            @PathVariable String id, @RequestBody String tag, @RequestBody Boolean toggle) {
        DocumentMetadataDTO metadata = documentService.toggleTag(id, tag, toggle);
        return ResponseEntity.ok(metadata);
    }

    @PutMapping("/{id}/metadata/tags")
    public ResponseEntity<DocumentMetadataDTO> setTags(
            @PathVariable String id, @RequestBody List<String> tags) {
        DocumentMetadataDTO metadata = documentService.setTags(id, tags);
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

    @PreAuthorize("@sec.isAuthorByRequest(#jwt, #uploaderId)")
    @GetMapping("/author/{uploaderId}")
    public ResponseEntity<List<DocumentMetadataDTO>> getByUploader(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String uploaderId) {
        return ResponseEntity.ok(documentService.findByUploaderId(uploaderId));
    }
    @GetMapping("/next-id")
    public ResponseEntity<String> generateNextId() {
        return ResponseEntity.ok(new ObjectId().toHexString());
    }

}
