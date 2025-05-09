package com.example.demo.controller;

import com.example.demo.dtos.ContactRequestDTO;
import com.example.demo.services.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/contact")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }


    @PostMapping
    @Operation(
            summary     = "Send contact message",
            description = "Accepts contact details from the user and forwards the message to the support team."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Message sent successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description  = "Server error while processing the message"
            )
    })
    public ResponseEntity<String> sendMessage(
            @Valid @RequestBody ContactRequestDTO dto
    ) {
        try {
            contactMessageService.submitContact(dto);
            return ResponseEntity.ok("Message has been sent.");
        } catch (Exception e) {
            log.error("Error sending contact message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while sending the message.");
        }
    }


}
