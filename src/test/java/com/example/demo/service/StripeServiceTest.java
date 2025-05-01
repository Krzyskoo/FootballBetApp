package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.CustomerService;
import com.example.demo.services.PaymentService;
import com.example.demo.services.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StripeServiceTest {
    @Mock
    private PaymentRepo paymentRepo;
    @Mock
    private CustomerService customerService;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private StripeService stripeService;

    @Test
    void shouldCreateCheckoutUrl() throws Exception {
        // Given
        Customer mockCustomer = Customer.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        Payment mockPayment = Payment.builder()
                .id(100L)
                .amount(BigDecimal.valueOf(10))
                .currency("usd")
                .status(PaymentStatus.PENDING)
                .customer(mockCustomer)
                .build();

        when(customerService.getAuthenticatedUsername()).thenReturn(1L);
        when(customerService.findById(1L)).thenReturn(mockCustomer);
        when(paymentRepo.save(any(Payment.class))).thenReturn(mockPayment);
        // Stub createCheckoutSession to avoid calling Stripe API
        StripeService spyService = Mockito.spy(stripeService);
        doReturn("http://mock-stripe-url.com").when(spyService)
                .createCheckoutSession(anyLong(), anyString(), anyString());

        // When
        String url = spyService.createCheckoutUrl(10L, "usd");

        // Then
        assertEquals("http://mock-stripe-url.com", url);
        verify(paymentRepo).save(any(Payment.class));
    }

    @Test
    void shouldHandleCheckoutSessionCompleted() throws StripeException {

        Session mockSession = mock(Session.class);
        when(mockSession.getMetadata()).thenReturn(Map.of("payment_id", "123"));
        when(mockSession.getPaymentIntent()).thenReturn("pi_456");

        Event.Data mockData = mock(Event.Data.class);
        when(mockData.getObject()).thenReturn(mockSession);

        Event mockEvent = mock(Event.class);
        when(mockEvent.getType()).thenReturn("checkout.session.completed");
        when(mockEvent.getData()).thenReturn(mockData);

        PaymentService paymentService = mock(PaymentService.class);

        StripeService stripeService = new StripeService(
                mock(PaymentRepo.class),
                mock(CustomerService.class),
                paymentService
        );


        ResponseEntity<String> response = stripeService.handleWebhook(mockEvent);


        verify(paymentService).updatePaymentStatus("123", "pi_456", "checkout.session.completed");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldHandlePaymentIntentSucceeded() throws StripeException {
        // Mock Stripe objects
        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getMetadata()).thenReturn(Map.of("payment_id", "321"));
        when(mockIntent.getId()).thenReturn("pi_321");

        // Mock inner static class: Event.Data
        Event.Data mockData = mock(Event.Data.class);
        when(mockData.getObject()).thenReturn(mockIntent); // this line is key

        Event mockEvent = mock(Event.class);
        when(mockEvent.getType()).thenReturn("payment_intent.succeeded");
        when(mockEvent.getData()).thenReturn(mockData); // don't let it return null

        // Act
        ResponseEntity<String> response = stripeService.handleWebhook(mockEvent);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService).updatePaymentStatus("321", "pi_321", "payment_intent.succeeded");
    }


    @Test
    void shouldIgnoreUnsupportedEventType() throws StripeException {
        // Given
        Event stripeEvent = mock(Event.class);
        when(stripeEvent.getType()).thenReturn("unrelated.event");

        // When
        ResponseEntity<String> response = stripeService.handleWebhook(stripeEvent);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyNoInteractions(paymentService);
    }

}
