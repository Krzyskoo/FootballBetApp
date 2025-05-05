package com.example.demo.services;

import com.example.demo.Dtos.CustomerDTO;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return customerRepo.findByEmail(authentication.getName()).get().getId();
    }

    public Customer save(Customer user) {
        if (customerRepo.findByEmail(user.getEmail()).isEmpty()){
            String hasPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hasPassword);
            user.setCreatedDt(new Date(System.currentTimeMillis()));
            return customerRepo.save(user);
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
