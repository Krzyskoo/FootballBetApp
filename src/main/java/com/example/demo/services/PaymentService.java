package com.example.demo.services;

import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repo.PaymentRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final CustomerService customerService;

    public PaymentService(PaymentRepo paymentRepo, CustomerService customerService) {
        this.paymentRepo = paymentRepo;
        this.customerService = customerService;
    }

    public void savePayment(Payment payment) {
        paymentRepo.save(payment);

    }
    public void updatePaymentStatus(String paymentIdInDatabase, String paymentId, String stripeEventType) {
        Optional<Payment> paymentOpt = paymentRepo.findById(Long.parseLong(paymentIdInDatabase));
        if (paymentOpt.isPresent() &&
                paymentOpt.get().getStatus().toString().equals("PENDING")&&
                stripeEventType.equals("checkout.session.completed")) {
            Payment payment = paymentOpt.get();
            payment.setStripePaymentId(paymentId);
            paymentRepo.save(payment);
        } else if (paymentOpt.isPresent() &&
                paymentOpt.get().getStatus().toString().equals("PENDING")
                && stripeEventType.equals("payment_intent.succeeded")) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.SUCCEEDED);
            paymentRepo.save(payment);
            customerService.updateBalance(paymentOpt.get().getCustomer().getId(), payment.getAmount());

        }

    }

}
