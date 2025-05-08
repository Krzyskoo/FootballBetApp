package com.example.demo.controller;

import com.example.demo.Dtos.RegisterRequestDTO;
import com.example.demo.Dtos.UserRegisteredEvent;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.kafka.KafkaProducerService;
import com.example.demo.model.Customer;
import com.example.demo.model.LoginRequestDTO;
import com.example.demo.model.LoginResponseDTO;
import com.example.demo.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    private final CustomerService customerService;
    private final KafkaProducerService kafkaProducerService;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    public UserController(CustomerService customerService, KafkaProducerService kafkaProducerService) {
        this.customerService = customerService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Operation(summary = "Rejestracja nowego użytkownika",
            description = "Rejestracja nowego użytkownika, po rejestracji wymagane logowanie oraz wysyłany jest email powitalny")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "User registration failed"),
        @ApiResponse(responseCode = "500", description = "An exception occured")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        logger.info("POST /register – próba rejestracji użytkownika o emailu={}", registerRequest.getEmail());
        try {
            Customer savedUser = customerService.save(registerRequest);
            if (savedUser.getId()>0){
                logger.debug("Użytkownik zapisany z ID={} – wysyłam event do Kafki", savedUser.getId());
                kafkaProducerService.sendUserRegisteredEvent(new UserRegisteredEvent(registerRequest.getEmail(), savedUser.getId()));
                return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
            }else {
                logger.warn("Rejestracja nie powiodła się – save() zwróciło ID<=0 dla emailu={}", registerRequest.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed");
            }
        }catch (Exception e){
            logger.error("Wystąpił wyjątek podczas rejestracji użytkownika email={}:", registerRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception occured: " + e.getMessage());
        }

    }
    @PostMapping("/login")
    @Operation(
            summary     = "Logowanie użytkownika",
            description = "Przyjmuje nazwę użytkownika i hasło, zwraca nagłówek z tokenem JWT i ciało z tokenem"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Logowanie udane, w nagłówku `" + ApplicationConstants.JWT_HEADER + "` zwrócono token",
                    content      = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = LoginResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Nieprawidłowe dane logowania"),
            @ApiResponse(responseCode = "500", description = "Wewnętrzny błąd serwera")
    })
    public ResponseEntity<LoginResponseDTO> apiLogin (@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dane logowania: username oraz password",
            required    = true,
            content     = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema    = @Schema(implementation = LoginRequestDTO.class)
            )
    )@RequestBody LoginRequestDTO loginRequest) {
        logger.info("POST /login – próba logowania user={}", loginRequest.username());
        String jwt = customerService.generateJWTToken(loginRequest.username(), loginRequest.password());
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }
}
