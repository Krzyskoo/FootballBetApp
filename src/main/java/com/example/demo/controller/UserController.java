package com.example.demo.controller;

import com.example.demo.constans.ApplicationConstans;
import com.example.demo.model.LoginRequestDTO;
import com.example.demo.model.LoginResponseDTO;
import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final CustomerRepo customerRepo;
    private final AuthenticationManager authenticationManager;
    @Value("${SecretKey}")
    private String secret;

    public UserController(PasswordEncoder passwordEncoder, CustomerRepo customerRepo, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepo = customerRepo;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer user) {
        try {
            String hasPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hasPassword);
            user.setCreatedDt(new Date(System.currentTimeMillis()));
            Customer savedUser = customerRepo.save(user);
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
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if(null != authenticationResponse && authenticationResponse.isAuthenticated()) {
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = Jwts.builder().issuer("FootballBetApp").subject("JWT Token")
                        .claim("username", authenticationResponse.getName())
                        .claim("authorities", authenticationResponse.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new java.util.Date())
                        .expiration(new java.util.Date((new java.util.Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();

        }
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstans.JWT_HEADER,jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }
}
