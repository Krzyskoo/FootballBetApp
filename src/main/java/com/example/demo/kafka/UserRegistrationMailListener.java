package com.example.demo.kafka;

import com.example.demo.Dtos.UserRegisteredEvent;
import com.example.demo.services.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationMailListener {
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.user-topic}", groupId = "mail-group")
    public void handleUserRegistered(String message){
        try {
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);
            emailService.sendWelcomeEmail(event);
        } catch (Exception e) {
            System.err.println("Error sending welcome email: " + e.getMessage());
        }
    }
}
