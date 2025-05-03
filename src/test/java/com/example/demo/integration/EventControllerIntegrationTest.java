package com.example.demo.integration;

import com.example.demo.Dtos.InternalEventDTO;
import com.example.demo.FootballPageApplication;
import com.example.demo.model.LoginRequestDTO;
import com.example.demo.model.LoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = FootballPageApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class EventControllerIntegrationTest {

    @LocalServerPort
    int port;

    TestRestTemplate rest = new TestRestTemplate();

    HttpHeaders headers;

    @BeforeEach
    void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void fullFlow_register_login_and_then_callSecuredEndpoint() {
        var regBody = Map.of(
                "email", "foo@test.com",
                "password", "P@ssword123"
        );
        ResponseEntity<String> regRes = rest.exchange(
                url("/register"),
                HttpMethod.POST,
                new HttpEntity<>(regBody, headers),
                String.class
        );
        assertThat(regRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        LoginRequestDTO login = new LoginRequestDTO("foo@test.com", "P@ssword123");
        ResponseEntity<LoginResponseDTO> loginRes = rest.exchange(
                url("/login"),
                HttpMethod.POST,
                new HttpEntity<>(login, headers),
                LoginResponseDTO.class
        );
        assertThat(loginRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        String jwt = loginRes.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertThat(jwt).isNotBlank();

        headers.set(HttpHeaders.AUTHORIZATION, jwt);

        ResponseEntity<InternalEventDTO[]> eventsRes = rest.exchange(
                url("/bet/events"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                InternalEventDTO[].class
        );

        assertThat(eventsRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(eventsRes.getBody()).isNotNull();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}