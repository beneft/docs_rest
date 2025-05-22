package com.project.signatureservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "documents-service", path = "/users")
public interface RecievedDocumentsClient {

    @GetMapping("/{userId}/documents")
    List<String> getDocuments(@PathVariable("userId") String userId);

    @PostMapping("/{userId}/documents")
    void addDocument(@PathVariable("userId") String userId,
                     @RequestParam("documentId") String documentId);

    @DeleteMapping("/{userId}/documents/{documentId}")
    void removeDocument(@PathVariable("userId") String userId,
                        @PathVariable("documentId") String documentId);
}
