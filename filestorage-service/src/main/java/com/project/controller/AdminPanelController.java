package com.project.controller;

import com.project.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
public class AdminPanelController {

    private final DocumentService documentService;

    @PostMapping ("/expiration-check")
    public ResponseEntity<String> downloadDocument() {
        documentService.expireDocuments();
        return ResponseEntity.ok("Documents expiration was recalculated");
    }
}
