package com.project.signatureservice.client;

import com.example.commondto.NcaVerifyRequest;
import com.example.commondto.NcaVerifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ncaNodeClient", url = "http://localhost:14579")
public interface NcaNodeClient {
    @PostMapping("/cms/verify")
    NcaVerifyResponse verifyCms(@RequestBody NcaVerifyRequest request);
}
