package com.project.signatureservice.kafka;

import com.example.commondto.DocumentBytesResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.*;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, DocumentBytesResponse> documentBytesResponseConsumerFactory() {
        JsonDeserializer<DocumentBytesResponse> deserializer = new JsonDeserializer<>(DocumentBytesResponse.class);
        deserializer.addTrustedPackages("com.example.commondto");

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "signatureservice");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DocumentBytesResponse> documentBytesResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DocumentBytesResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(documentBytesResponseConsumerFactory());
        return factory;
    }
}