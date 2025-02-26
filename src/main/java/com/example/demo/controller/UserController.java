package com.example.demo.controller;

import com.example.demo.constans.ApplicationConstans;
import com.example.demo.model.Customer;
import com.example.demo.model.LoginRequestDTO;
import com.example.demo.model.LoginResponseDTO;
import com.example.demo.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;
    private final AuthenticationManager authenticationManager;

    public UserController(PasswordEncoder passwordEncoder, CustomerService customerService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.customerService = customerService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer user) {
        try {
            String hasPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hasPassword);
            user.setCreatedDt(new Date(System.currentTimeMillis()));
            Customer savedUser = customerService.save(user);
            if (savedUser.getId()>0){
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
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstans.JWT_HEADER,jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }

    @GetMapping("/secured")
    public ResponseEntity<String> securedEndpoint() {
        return ResponseEntity.status(HttpStatus.OK).body("Secured endpoint accessed successfully");
    }
}
