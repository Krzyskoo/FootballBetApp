package com.example.demo.integration;

import com.example.demo.dtos.BetRequest;
import com.example.demo.dtos.BetSelectionRequest;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.model.Event;
import com.example.demo.model.LoginRequestDTO;
import com.example.demo.model.Result;
import com.example.demo.repo.BetRepo;
import com.example.demo.repo.CustomerRepo;
import com.example.demo.repo.EventRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BetControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private BetRepo betRepo;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "email":"user@test.com", "password":"P@ssw0rd!" }
                            """))
                .andExpect(status().isCreated());

        var loginJson = objectMapper.writeValueAsString(
                new LoginRequestDTO("user@test.com","P@ssw0rd!"));
        var mvcLogin = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        jwtToken = mvcLogin.getResponse().getHeader(ApplicationConstants.JWT_HEADER);
        assertThat(jwtToken).isNotBlank();

        var cust = customerRepo.findByEmail("user@test.com")
                .orElseThrow();
        cust.setBalance(new BigDecimal("1000"));
        customerRepo.save(cust);

        Event ev = Event.builder()
                .eventId("evt-123")
                .sportKey("soccer_epl")
                .sportTitle("Premier League")
                .startTime(new Date(System.currentTimeMillis() + 60_000)) // +1min
                .homeTeam("Man Utd")
                .homeTeamOdds("1.8")
                .awayTeam("Chelsea")
                .awayTeamOdds("2.0")
                .drawOdds("3.5")
                .status(Result.PENDING)
                .completed(false)
                .build();
        eventRepo.save(ev);
    }
    @AfterEach
    void tearDown() {
        betRepo.deleteAll();
        eventRepo.deleteAll();
        customerRepo.deleteAll();
    }



    @Test
    @Order(1)
    @DisplayName("POST /bets/place → 201 + zapisany zakład")
    void placeBet_success_then201() throws Exception {
        BetSelectionRequest selReq = new BetSelectionRequest("evt-123", Result.HOME_WIN);
        BetRequest betReq = new BetRequest(new BigDecimal("100"), List.of(selReq));
        String json = objectMapper.writeValueAsString(betReq);

        mockMvc.perform(post("/bets/place")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").isNumber())
                .andExpect(jsonPath("$.stake").value(100))
                .andExpect(jsonPath("$.totalOdds").value(1.8));

        mockMvc.perform(get("/bets")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stake").value(100))
                .andExpect(jsonPath("$[0].totalOdds").value(1.8));
    }

    @Test
    @Order(2)
    @DisplayName("POST /bets/place → 400 gdy niewystarczające środki")
    void placeBet_insufficientBalance_then400() throws Exception {
        var cust = customerRepo.findByEmail("user@test.com").orElseThrow();
        cust.setBalance(new BigDecimal("10"));
        customerRepo.save(cust);

        BetRequest betReq = new BetRequest(new BigDecimal("100"),
                List.of(new BetSelectionRequest("evt-123", Result.HOME_WIN)));
        String json = objectMapper.writeValueAsString(betReq);

        mockMvc.perform(post("/bets/place")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
