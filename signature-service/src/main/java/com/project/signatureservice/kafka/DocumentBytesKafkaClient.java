package com.project.signatureservice.kafka;


import com.example.commondto.DocumentBytesRequest;
import com.example.commondto.DocumentBytesResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class DocumentBytesKafkaClient {

    private final KafkaTemplate<String, DocumentBytesRequest> documentBytesRequestKafkaTemplate;

    private final ConcurrentMap<String, CompletableFuture<DocumentBytesResponse>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<DocumentBytesResponse> requestBytes(String documentId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<DocumentBytesResponse> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        documentBytesRequestKafkaTemplate.send("document-bytes-request", correlationId, new DocumentBytesRequest(documentId));
        return future;
    }

    @KafkaListener(
            topics = "document-bytes-response",
            groupId = "signatureservice",
            containerFactory = "documentBytesResponseKafkaListenerContainerFactory"
    )
    public void listenResponse(ConsumerRecord<String, DocumentBytesResponse> record) {
        CompletableFuture<DocumentBytesResponse> future = pendingRequests.remove(record.key());
        if (future != null) {
            future.complete(record.value());
        }
    }
}
