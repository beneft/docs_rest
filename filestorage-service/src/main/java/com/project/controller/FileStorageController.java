package com.project.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.project.dto.SignRequest;
import com.project.model.Signer;
import com.project.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileStorageController {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GridFsOperations gridFsOperations;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileId = fileStorageService.storeFile(file);
        return ResponseEntity.ok("File uploaded successfully, ID: " + fileId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) throws IOException {
        GridFSFile file = fileStorageService.getFile(id);
        if (file == null) return ResponseEntity.notFound().build();

        GridFsResource resource = gridFsOperations.getResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getMetadata().getString("_contentType")))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listFiles() {
        return ResponseEntity.ok(fileStorageService.listFiles());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable String id) {
        fileStorageService.deleteFile(id);
        return ResponseEntity.ok("File deleted successfully.");
    }

    @PostMapping("/signers")
    public ResponseEntity<String> addSigner(@RequestBody SignRequest request) {
        fileStorageService.addSigner(request);
        return ResponseEntity.ok("User added as signer");
    }

    @PostMapping("/sign")
    public ResponseEntity<String> signDocument(@RequestBody SignRequest request) {
        return ResponseEntity.ok(fileStorageService.signDocument(request));
    }

    @GetMapping("/signers/{documentId}")
    public ResponseEntity<List<Signer>> getSigners(@PathVariable String documentId) {
        return ResponseEntity.ok(fileStorageService.getSigners(documentId));
    }
}
