package com.project.signatureservice.kafka;


import com.example.commondto.DocumentBytesRequest;
import com.example.commondto.DocumentBytesResponse;
import com.example.commondto.NotificationRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.*;

@Configuration
public class KafkaConfig {

    private Map<String, Object> baseProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put("spring.json.trusted.packages", "com.example.commondto");
        return props;
    }

    @Bean
    public KafkaTemplate<String, NotificationRequest> notificationKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(baseProducerProps()));
    }

    @Bean
    public KafkaTemplate<String, DocumentBytesRequest> documentBytesRequestKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(baseProducerProps()));
    }

    @Bean
    public KafkaTemplate<String, DocumentBytesResponse> documentBytesResponseKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(baseProducerProps()));
    }
}
