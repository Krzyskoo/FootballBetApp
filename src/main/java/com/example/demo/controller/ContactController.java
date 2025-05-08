package com.example.demo.controller;

import com.example.demo.Dtos.ContactRequestDTO;
import com.example.demo.services.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }


    @PostMapping
    @Operation(
            summary     = "Wyślij wiadomość kontaktową",
            description = "Przyjmuje dane kontaktowe od użytkownika i wysyła wiadomość do zespołu supportu."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wiadomość została wysłana pomyślnie"),
            @ApiResponse(responseCode = "500", description = "Błąd serwera podczas przetwarzania wiadomości")
    })
    public ResponseEntity<String> sendMessage(@RequestBody ContactRequestDTO dto) {
        try {
            contactMessageService.submitContact(dto);
            return ResponseEntity.ok("Wiadomość została wysłana.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Wystąpił błąd podczas wysyłania wiadomości.");
        }
    }

}
