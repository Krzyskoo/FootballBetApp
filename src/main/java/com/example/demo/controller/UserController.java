package com.example.demo.controller;

import com.example.demo.constants.ApplicationConstants;
import com.example.demo.dtos.CustomerDTO;
import com.example.demo.dtos.RegisterRequestDTO;
import com.example.demo.dtos.UserRegisteredEvent;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(
            summary     = "Register a new user",
            description = "Registers a new user; requires login after registration and sends a welcome email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User registration failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        logger.info("POST /register – attempt to register user with email={}", registerRequest.getEmail());
        try {
            Customer savedUser = customerService.save(registerRequest);
            if (savedUser.getId()>0){
                logger.debug("User saved with ID={} – sending UserRegistered event to Kafka", savedUser.getId());
                kafkaProducerService.sendUserRegisteredEvent(new UserRegisteredEvent(registerRequest.getEmail(), savedUser.getId()));
                return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
            }else {
                logger.warn("Registration failed – save() returned ID<=0 for email={}", registerRequest.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed");
            }
        }catch (Exception e){
            logger.error("Exception occurred while registering user email={}:", registerRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception occured: " + e.getMessage());
        }

    }
    @PostMapping("/login")
    @Operation(
            summary     = "User login",
            description = "Accepts username and password and returns a JWT token in both the response header and body."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Login successful; JWT token returned in header `" + ApplicationConstants.JWT_HEADER + "`",
                    content      = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = LoginResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid login credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoginResponseDTO> apiLogin (@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login data: username and password",
            required    = true,
            content     = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema    = @Schema(implementation = LoginRequestDTO.class)
            )
    )@RequestBody LoginRequestDTO loginRequest) {
        logger.info("POST /login – attempt to login user={}", loginRequest.username());
        String jwt = customerService.generateJWTToken(loginRequest.username(), loginRequest.password());
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }
    @GetMapping("/customer")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary     = "Get current customer",
            description = "Returns the profile information of the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Customer data retrieved successfully",
                    content      = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = CustomerDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description  = "Unauthorized",
                    content      = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "500",
                    description  = "Internal server error",
                    content      = @Content(schema = @Schema())
            )
    })
    public ResponseEntity<CustomerDTO> securedEndpoint() {
        log.info("Fetching current authenticated customer");
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomer());
    }
}
