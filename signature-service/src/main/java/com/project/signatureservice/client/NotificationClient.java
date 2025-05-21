package com.project.signatureservice.client;

import com.example.commondto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "http://localhost:8085")
public interface NotificationClient {
    @PostMapping("/notify")
    void notifySigners(@RequestBody NotificationRequest request);
}
