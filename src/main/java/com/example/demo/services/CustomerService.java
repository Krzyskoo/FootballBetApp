package com.example.demo.services;

import com.example.demo.dtos.CustomerDTO;
import com.example.demo.dtos.RegisterRequestDTO;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerService {

    private final CustomerRepo customerRepo;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;


    public CustomerService(CustomerRepo customerRepo, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, Environment env) {
        this.customerRepo = customerRepo;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }


    public String generateJWTToken(String username, String password) {
        log.info("Generating JWT token for user {}", username);
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY);
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username,
                password);
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
        return jwt;
    }
    public Long getAuthenticatedUsername() {
        log.info("Getting authenticated username");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return customerRepo.findByEmail(authentication.getName()).get().getId();
    }

    public Customer save(RegisterRequestDTO registerRequest) {
        if (customerRepo.findByEmail(registerRequest.getEmail()).isEmpty()){
            String hasPassword = passwordEncoder.encode(registerRequest.getPassword());
            Customer customer = new Customer(registerRequest.getEmail(), hasPassword, BigDecimal.ZERO);
            return customerRepo.save(customer);
        }else throw new IllegalArgumentException("Email already exists");

    }

    public Customer findById(Long id) {
        return customerRepo.findById(id).orElse(null);
    }


    public CustomerDTO getCustomer() {
        return customerRepo.findById(getAuthenticatedUsername())
                .map(customer -> new CustomerDTO(
                        customer.getEmail(),
                        customer.getBalance()))
                .orElse(null);
    }

}
