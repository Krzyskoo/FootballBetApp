# FootballBetApp

**FootballBetApp** is a Spring Boot powered web application that simulates an online football betting platform. It provides a RESTful API for users to register, authenticate, deposit funds, and place bets on football matches, while administrators can manage matches and oversee bets. The project showcases a robust backend architecture with secure authentication, data persistence, third-party integrations, and documentation, making it ideal for demonstration in a CV or portfolio.

## Table of Contents

- [Project Overview](#project-overview)  
- [Core Features](#core-features)  
- [Technologies Used](#technologies-used)  
- [System Architecture](#system-architecture)  
- [Installation and Setup](#installation-and-setup)  
- [Configuration](#configuration)  
- [API Usage (Endpoints Guide)](#api-usage-endpoints-guide)  
- [Database Structure](#database-structure)  
- [Example Use Cases](#example-use-cases)  
- [Testing](#testing)  
- [Contribution Guidelines](#contribution-guidelines)  
- [License](#license)  
- [Contact Information](#contact-information)  

## Project Overview

This project was created to explore and demonstrate building a full-stack betting application using modern Java technologies. The application enables typical betting platform use-cases such as user sign-up/login, browsing available matches, placing bets on match outcomes, and administrative control over events and users. Key goals of the project include:

- **End-to-End Functionality**: From database design and business logic to API exposure and external service integration.  
- **Clean Architecture**: Separation of concerns into layers (controllers, services, repositories).  
- **Security & Reliability**: JWT-based authentication, role-based access control, and comprehensive testing.  
- **Integrations & Tooling**: Swagger for API docs, Docker Compose for setup, Kafka, Stripe, and SendGrid integrations.

## Core Features

- **User Management**: Registration, JWT authentication, encrypted passwords.  
- **Betting Mechanics**: Browse matches, place single/multiple bets, track bet status.  
- **Match Administration**: Admin users can create matches, update results, and settle bets.  
- **Wallet & Payments**: Simulated deposits via Stripe, Stripe webhook handling.  
- **Email Notifications**: SendGrid integration for welcome emails and bet outcome alerts.  
- **Event Streaming**: Apache Kafka events for user registration and bet placement.  
- **API Documentation**: Interactive Swagger UI.  
- **Testing**: Unit and integration tests with JUnit, Mockito, H2 database, and CI via GitHub Actions.

## Technologies Used

- **Spring Boot 3** (Web, Data JPA, Security, OpenFeign, DevTools)  
- **Database**: PostgreSQL (or MySQL/H2) via Spring Data JPA & Hibernate  
- **Swagger/OpenAPI** (Springdoc)  
- **Apache Kafka** (Spring for Apache Kafka)  
- **Stripe Java SDK** for payment processing  
- **SendGrid & Spring Mail** for email  
- **Lombok** for boilerplate reduction  
- **MapStruct** for DTO mappings  
- **JUnit 5 & Mockito** for testing  
- **Maven** for build and dependency management  
- **Docker & Docker Compose** for containers  
- **GitHub Actions** for CI/CD  

## System Architecture

1. **API Layer** (`@RestController`): Defines HTTP endpoints, request validation, response codes.  
2. **Service Layer**: Business logic (user registration, bet processing, payment handling), transactions, external API calls.  
3. **Data Access** (`Spring Data JPA`): Repository interfaces and JPA entities for `User`, `Bet`, `Match`, etc.  
4. **Database**: Relational schema with entities and relationships (ACID compliance).  
5. **Security**: JWT tokens, password hashing (BCrypt), role-based route protection.  
6. **Event & Integration**: Kafka producers, Stripe payment intents & webhook, SendGrid email service.  

## Installation and Setup

Ensure you have **Java 17+**, **Maven**, and **Docker** installed.

1. **Clone the repo**  
   ```bash
   git clone https://github.com/Krzyskoo/FootballBetApp.git
   cd FootballBetApp
   
2**Configure Environment**
Default settings in application.properties work with local MySQL on localhost:3306. Override via environment variables as needed.

3**Start Dependencies**
In docker-compose.yml, ensure services for Kafka/Zookeeper and optionally a database are defined.

4**Access**
API Base: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui/index.html
Kafka UI (if enabled): http://localhost:8181

5**Configuration**
Database (MySQL default)
spring.datasource.url=jdbc:mysql://${DATABASE_HOST:localhost}:${DATABASE_PORT:3306}/${DATABASE_NAME:FootballDatabase}
spring.datasource.username=${DATABASE_USERNAME:root}
spring.datasource.password=${DATABASE_PASSWORD:root}
spring.jpa.hibernate.ddl-auto=update

Server
server.port=8080

JWT & Security
(e.g.) jwt.secret=${JWT_SECRET:your_jwt_secret}

Stripe
StripeKey=${STRIPE_SECRET_KEY:sk_test_xxx}
stripe.webhook.secret=${STRIPE_WEBHOOK_SECRET:whsec_xxx}

SendGrid
SENDGRID_API_KEY in env

Kafka
spring.kafka.bootstrap-servers=${KAFKA_BROKER:localhost:9092}
app.kafka.enabled=true
app.kafka.user-topic=user-registered

6**API Usage (Endpoints Guide)**
Explore full API in Swagger UI api-documentation.pdf. 

7**Database Structure** 
![database](https://github.com/user-attachments/assets/37c5d94c-e695-4a94-a75f-479ac2b386c1)

Contact Information
Author: Krzyskoo

GitHub: github.com/Krzyskoo/FootballBetApp

Email:krzysztof_kandyba@o2.pl

LinkedIn: [https://www.linkedin.com/in/yourprofile](https://www.linkedin.com/in/krzysztof-kandyba-633914213/)

