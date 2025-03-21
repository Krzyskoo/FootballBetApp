package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableFeignClients(basePackages = "com.example.demo.proxy")
public class FootballPageApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballPageApplication.class, args);
	}

}
