package com.example.demo.services;

import com.example.demo.Dtos.ContactRequestDTO;
import com.example.demo.model.ContactMessage;
import com.example.demo.repo.ContactMessageRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
@Slf4j
@Service
public class ContactMessageService {
    private final ContactMessageRepo messageRepository;
    private final EmailService emailService;

    public ContactMessageService(ContactMessageRepo messageRepository, EmailService emailService) {
        this.messageRepository = messageRepository;
        this.emailService = emailService;
    }

    public void submitContact(ContactRequestDTO dto) throws IOException {
        ContactMessage message = new ContactMessage();
        message.setName(dto.getName());
        message.setEmail(dto.getEmail());
        message.setSubject(dto.getSubject());
        message.setMessage(dto.getMessage());
        message.setSubmittedAt(LocalDateTime.now());

        messageRepository.save(message);

        String html = emailService.loadHtmlTemplate(dto);
        emailService.sendEmail("📬 Formularz kontaktowy: " + dto.getSubject(), html);

    }
}
