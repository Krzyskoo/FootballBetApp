package com.example.demo.controller;

import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.PaymentService;
import com.example.demo.services.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentWebhookController {
    private final PaymentRepo paymentRepo;
    private final PaymentService paymentService;
    private final StripeService stripeService;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    public PaymentWebhookController(PaymentRepo paymentRepo, PaymentService paymentService, StripeService stripeService) {
        this.paymentRepo = paymentRepo;
        this.paymentService = paymentService;
        this.stripeService = stripeService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {

            Event stripeEvent = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
            return stripeService.handleWebhook(stripeEvent);

        /*
        String eventType = (String) payload.get("type");

        if ("checkout.session.completed".equals(eventType)) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> object = (Map<String, Object>) data.get("object");
            String stripePaymentId = (String) object.get("id");
            String stripeDatabaseId = (String) data.get("metadata");
            paymentService.updatePaymentStatus(stripePaymentId);
        }
        /*
         */

    }

}
