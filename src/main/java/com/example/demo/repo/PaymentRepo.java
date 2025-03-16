package com.example.demo.repo;

import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {


    Optional<Payment> findByStripePaymentId(String stripePaymentId);
    Optional<Payment> findByCustomer(Customer customer);


}
