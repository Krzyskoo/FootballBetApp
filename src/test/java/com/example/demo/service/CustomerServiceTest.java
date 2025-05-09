package com.example.demo.service;

import com.example.demo.dtos.CustomerDTO;
import com.example.demo.dtos.RegisterRequestDTO;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.model.Customer;
import com.example.demo.model.TransactionType;
import com.example.demo.repo.BalanceHistoryRepo;
import com.example.demo.repo.CustomerRepo;
import com.example.demo.services.BalanceHistoryService;
import com.example.demo.services.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomerServiceTest {

    @Mock
    private CustomerRepo customerRepo;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Environment env;
    @Mock
    private BalanceHistoryRepo balanceHistoryRepo;

    @InjectMocks
    private BalanceHistoryService balanceHistoryService;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldSaveNewCustomer_whenEmailNotExists() {
        RegisterRequestDTO customer = RegisterRequestDTO.builder()
                .email("test@example.com")
                .password("plainPassword")
                .build();

        when(customerRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(customerRepo.save(any(Customer.class))).thenAnswer(i -> i.getArguments()[0]);

        Customer saved = customerService.save(customer);

        assertEquals("encodedPassword", saved.getPassword());
        assertEquals("test@example.com", saved.getEmail());
        verify(customerRepo).save(saved);
    }
    @Test
    void shouldThrowException_whenEmailExists() {
        Customer existing = new Customer();
        existing.setEmail("test@example.com");

        when(customerRepo.findByEmail("test@example.com")).thenReturn(Optional.of(existing));

        RegisterRequestDTO newCustomer = RegisterRequestDTO.builder()
                .email("test@example.com")
                .password("pass")
                .build();

        assertThrows(IllegalArgumentException.class, () -> customerService.save(newCustomer));
    }
    @Test
    void shouldUpdateBalance_whenCustomerExists() {
        Customer customer = new Customer();
        customer.setBalance(new BigDecimal("100.00"));

        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        customer= customerRepo.findById(1L).orElse(null);
        BigDecimal balance = customer.getBalance();
        balanceHistoryService.saveBalanceChange(customer, TransactionType.DEPOSIT, new BigDecimal("50.00"),"TestDeposit");

        assertEquals(new BigDecimal("150.00"), customer.getBalance());
        verify(customerRepo).save(customer);
    }

    @Test
    void shouldReturnCustomerById() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertEquals(1L, result.getId());
    }
    @Test
    void shouldGenerateJwtToken_whenCredentialsAreValid() {
        String email = "test@example.com";
        String password = "password123";
        String secret = "my-jwt-secret-my-jwt-secret-my-jwt-secret";

        Authentication unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(email, password);
        Authentication authenticated = new UsernamePasswordAuthenticationToken(email, password, List.of(() -> "ROLE_USER"));

        when(env.getProperty(ApplicationConstants.JWT_SECRET_KEY)).thenReturn(secret);
        when(authenticationManager.authenticate(any())).thenReturn(authenticated);

        String jwt = customerService.generateJWTToken(email, password);

        assertNotNull(jwt);
        assertTrue(jwt.startsWith("ey")); // base64
    }
    @Test
    void shouldReturnCustomerDto_whenAuthenticatedUserExists() {
        // given
        String email = "test@example.com";
        BigDecimal balance = new BigDecimal("150.00");

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .balance(balance)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, List.of());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(customerRepo.findByEmail(email)).thenReturn(Optional.of(customer));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        // when
        CustomerDTO result = customerService.getCustomer();

        // then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(balance, result.getBalance());
    }
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }





}
