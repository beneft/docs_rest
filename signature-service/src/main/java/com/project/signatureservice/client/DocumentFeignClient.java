package com.project.signatureservice.client;

import com.example.commondto.DocumentMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "document-service", url = "${document.service.url}")
public interface DocumentFeignClient {

    @GetMapping("/documents/internal/{id}/bytes")
    ResponseEntity<byte[]> getDocumentBytes(@PathVariable("id") String id);

    @GetMapping("/documents/{id}/metadata")
    ResponseEntity<DocumentMetadataDTO> getDocumentMetadata(@PathVariable("id") String id);
}
