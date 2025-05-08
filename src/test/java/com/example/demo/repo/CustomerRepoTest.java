package com.example.demo.repo;

import com.example.demo.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CustomerRepoTest {

    @Autowired
    private CustomerRepo customerRepo;

    @Test
    void shouldFindCustomerById() {
        Customer customer = new Customer();
        customer.setEmail("another@example.com");
        customer.setPassword("password");
        customerRepo.save(customer);

        Optional<Customer> foundCustomer = customerRepo.findById(customer.getId()); // <1>

        assertThat(foundCustomer).isPresent();
    }
    @Test
    void shouldFindCustomerByEmail() {
        Customer customer = new Customer();
        customer.setEmail("another2@example.com");
        customer.setPassword("password");
        customerRepo.save(customer);

        Optional<Customer> foundCustomer = customerRepo.findByEmail(customer.getEmail()); // <1>

        assertThat(foundCustomer).isPresent();
    }
}
