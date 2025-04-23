package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableFeignClients(basePackages = "com.example.demo.proxy")
@ComponentScan(basePackages = {"com.example.demo","com.example.demo.mapper"})
@EnableScheduling
public class FootballPageApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballPageApplication.class, args);
	}

}
