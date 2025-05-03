package com.example.demo.controller;

import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.PaymentService;
import com.example.demo.services.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentWebhookControllerTest {
    private PaymentWebhookController controller;
    private StripeService stripeService;
    private PaymentService paymentService;
    private PaymentRepo paymentRepo;

    @BeforeEach
    void setUp() {
        stripeService  = mock(StripeService.class);
        paymentService = mock(PaymentService.class);
        paymentRepo    = mock(PaymentRepo.class);

        controller = new PaymentWebhookController(paymentRepo, paymentService, stripeService);
        ReflectionTestUtils.setField(controller, "stripeWebhookSecret", "whsec_test_secret");
    }

    @Test
    void validSignature_delegatesToService() throws Exception {
        String payload   = "{\"id\":\"evt_test\"}";
        String sigHeader = "stripe-signature";

        Event fakeEvent = new Event();
        fakeEvent.setId("evt_test");

        try (MockedStatic<Webhook> ws = mockStatic(Webhook.class)) {
            ws.when(() -> Webhook.constructEvent(payload, sigHeader, "whsec_test_secret"))
                    .thenReturn(fakeEvent);

            when(stripeService.handleWebhook(fakeEvent))
                    .thenReturn(ResponseEntity.ok("handled"));

            ResponseEntity<String> resp = controller.handleWebhook(payload, sigHeader);

            assertEquals(200, resp.getStatusCodeValue());
            assertEquals("handled", resp.getBody());
            verify(stripeService).handleWebhook(fakeEvent);
        }
    }

    @Test
    void invalidSignature_throwsSignatureException() {
        String payload   = "{\"id\":\"evt_bad\"}";
        String sigHeader = "bad-signature";

        try (MockedStatic<Webhook> ws = mockStatic(Webhook.class)) {
            ws.when(() -> Webhook.constructEvent(payload, sigHeader, "whsec_test_secret"))
                    .thenThrow(new SignatureVerificationException("bad signature", sigHeader));

            assertThrows(SignatureVerificationException.class, () ->
                    controller.handleWebhook(payload, sigHeader)
            );
        }
    }
}
