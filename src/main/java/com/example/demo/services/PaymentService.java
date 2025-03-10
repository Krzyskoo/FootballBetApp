package com.example.demo.services;

import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repo.PaymentRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;

    public PaymentService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Transactional
    public void savePayment(Payment payment) {
        Payment savedPayment = Payment.builder()
                .stripePaymentId(payment.getStripePaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(PaymentStatus.PENDING)
                .customer(payment.getCustomer())
                .customerEmail(payment.getCustomerEmail())
                .paymentMethod("card")
                .build();
        paymentRepo.save(savedPayment);

    }
    public void updatePaymentStatus(String stripePaymentId) {
        Optional<Payment> paymentOpt = paymentRepo.findByStripePaymentId(stripePaymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.SUCCEEDED);
            paymentRepo.save(payment);
        }

    }
}
