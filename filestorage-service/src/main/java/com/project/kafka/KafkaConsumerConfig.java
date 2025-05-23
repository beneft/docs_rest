package com.project.kafka;

import com.example.commondto.DocumentBytesRequest;
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
    public ConsumerFactory<String, DocumentBytesRequest> documentBytesRequestConsumerFactory() {
        JsonDeserializer<DocumentBytesRequest> deserializer = new JsonDeserializer<>(DocumentBytesRequest.class, false);
        deserializer.addTrustedPackages("com.example.commondto");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "documentservice");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DocumentBytesRequest> documentBytesKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DocumentBytesRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(documentBytesRequestConsumerFactory());
        return factory;
    }
}
