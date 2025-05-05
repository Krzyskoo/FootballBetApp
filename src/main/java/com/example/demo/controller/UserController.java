package com.example.demo.controller;

import com.example.demo.Dtos.CustomerDTO;
import com.example.demo.Dtos.UserRegisteredEvent;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.kafka.KafkaProducerService;
import com.example.demo.model.Customer;
import com.example.demo.model.LoginRequestDTO;
import com.example.demo.model.LoginResponseDTO;
import com.example.demo.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final CustomerService customerService;
    private final KafkaProducerService kafkaProducerService;

    public UserController(CustomerService customerService, KafkaProducerService kafkaProducerService) {
        this.customerService = customerService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer user) {
        try {
            Customer savedUser = customerService.save(user);
            if (savedUser.getId()>0){
                kafkaProducerService.sendUserRegisteredEvent(new UserRegisteredEvent(user.getEmail(), savedUser.getId()));
                return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception occured: " + e.getMessage());
        }

    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> apiLogin (@RequestBody LoginRequestDTO loginRequest) {
        String jwt = customerService.generateJWTToken(loginRequest.username(), loginRequest.password());
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }

    @GetMapping("/customer")
    public ResponseEntity<CustomerDTO> securedEndpoint() {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomer());
    }
}
