package com.example.demo.controller;

import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentWebhookController {
    private final PaymentRepo paymentRepo;
    private final PaymentService paymentService;

    public PaymentWebhookController(PaymentRepo paymentRepo, PaymentService paymentService) {
        this.paymentRepo = paymentRepo;
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        String eventType = (String) payload.get("type");

        if ("checkout.session.completed".equals(eventType)) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> object = (Map<String, Object>) data.get("object");
            String stripePaymentId = (String) object.get("id");
            paymentService.updatePaymentStatus(stripePaymentId);
        }
        return ResponseEntity.ok("OK");
    }

}
