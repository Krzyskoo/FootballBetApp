package com.example.demo.config;

import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FootballAppUserDetailsService implements UserDetailsService {

    private final CustomerRepo customerRepo;

    public FootballAppUserDetailsService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<GrantedAuthority> authorities = customer.getAuthorities().stream().map(authority -> (GrantedAuthority) () -> authority.getName()).collect(Collectors.toList());
        return new User(customer.getEmail(), customer.getPassword(), authorities);
    }
}
