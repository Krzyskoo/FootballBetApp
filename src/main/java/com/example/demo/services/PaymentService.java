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
    private final CustomerService customerService;

    public PaymentService(PaymentRepo paymentRepo, CustomerService customerService) {
        this.paymentRepo = paymentRepo;
        this.customerService = customerService;
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
    public void finalUpdatePaymentStatus(String paymentIdInDatabase, String paymentId) {
        Optional<Payment> paymentOpt = paymentRepo.findById(Long.parseLong(paymentIdInDatabase));
        if (paymentOpt.isPresent()&& paymentOpt.get().getStatus().toString().equals("PENDING")) {
            Payment payment = paymentOpt.get();
            payment.setStripePaymentId(paymentId);
            payment.setStatus(PaymentStatus.SUCCEEDED);
            paymentRepo.save(payment);
            customerService.updateBalance(payment.getCustomer().getId(), payment.getAmount());
        }



    }
}
