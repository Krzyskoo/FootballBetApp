package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.CustomerService;
import com.example.demo.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldUpdateStripePaymentId_whenCheckoutSessionCompleted() {
        // Given
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepo.findById(1L)).thenReturn(Optional.of(payment));

        // When
        paymentService.updatePaymentStatus("1", "stripe123", "checkout.session.completed");

        // Then
        assertEquals("stripe123", payment.getStripePaymentId());
        verify(paymentRepo).save(payment);
        verify(customerService, never()).updateBalance(any(), any());
    }

    @Test
    void shouldUpdateStatusAndBalance_whenPaymentIntentSucceeded() {
        // Given
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("100.00"))
                .customer(Customer.builder().id(5L).build())
                .build();

        when(paymentRepo.findById(1L)).thenReturn(Optional.of(payment));

        // When
        paymentService.updatePaymentStatus("1", "unusedStripeId", "payment_intent.succeeded");

        // Then
        assertEquals(PaymentStatus.SUCCEEDED, payment.getStatus());
        verify(paymentRepo).save(payment);
        verify(customerService).updateBalance(5L, new BigDecimal("100.00"));
    }

    @Test
    void shouldNotUpdate_whenStatusNotPending() {
        // Given
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.SUCCEEDED)
                .build();

        when(paymentRepo.findById(1L)).thenReturn(Optional.of(payment));

        // When
        paymentService.updatePaymentStatus("1", "irrelevant", "payment_intent.succeeded");

        // Then
        verify(paymentRepo, never()).save(any());
        verify(customerService, never()).updateBalance(any(), any());
    }

    @Test
    void shouldDoNothing_whenPaymentNotFound() {
        // Given
        when(paymentRepo.findById(999L)).thenReturn(Optional.empty());

        // When
        paymentService.updatePaymentStatus("999", "someStripeId", "checkout.session.completed");

        // Then
        verify(paymentRepo, never()).save(any());
        verify(customerService, never()).updateBalance(any(), any());
    }
}
