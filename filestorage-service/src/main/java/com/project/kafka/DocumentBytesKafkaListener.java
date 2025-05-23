package com.project.kafka;


import com.example.commondto.DocumentBytesRequest;
import com.example.commondto.DocumentBytesResponse;
import com.project.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DocumentBytesKafkaListener {

    private final DocumentService documentService;
    private final KafkaTemplate<String, DocumentBytesResponse> kafkaTemplate;

    @KafkaListener(
            topics = "document-bytes-request",
            groupId = "documentservice",
            containerFactory = "documentBytesKafkaListenerContainerFactory"
    )
    public void handleRequest(ConsumerRecord<String, DocumentBytesRequest> record){
        String key = record.key();
        String docId = record.value().getDocumentId();

        byte[] bytes = documentService.downloadDocument(docId)
                .map(resource -> {
                    try {
                        return StreamUtils.copyToByteArray(resource.getInputStream());
                    } catch (IOException e) {
                        return null;
                    }
                }).orElse(null);

        kafkaTemplate.send("document-bytes-response", key, new DocumentBytesResponse(docId, bytes));
    }
}