package com.project.templateservice.feign;

import com.project.templateservice.dto.UploadDocumentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "DOCUMENT-SERVICE", url = "http://localhost:8082/documents")
public interface DocumentFeignClient {

    @PostMapping(
            value = "/template",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    ResponseEntity<String> sendFilledDocument(
            @RequestHeader("X-File-Name") String name,
            @RequestHeader("X-Content-Type") String contentType,
            @RequestBody byte[] data
    );
}