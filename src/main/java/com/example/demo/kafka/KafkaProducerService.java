package com.example.demo.kafka;

import com.example.demo.Dtos.UserRegisteredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Environment env;

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(env.getProperty("app.kafka.user-topic"), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not send user registered event", e);
        }
    }

}
