package com.example.demo.controller;

import com.example.demo.dtos.RegisterRequestDTO;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.kafka.KafkaProducerService;
import com.example.demo.model.Customer;
import com.example.demo.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
class UserControllerTest {


    @Mock
    private CustomerService customerService;
    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    void registerUser_whenSavedWithIdGreaterThanZero_thenReturns201() throws Exception {
        Customer saved = new Customer();
        saved.setId(42L);
        saved.setEmail("test@example.com");
        when(customerService.save(any(RegisterRequestDTO.class))).thenReturn(saved);


        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email":"test@example.com",
                          "password":"secret"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));

        verify(customerService, times(1)).save(any(RegisterRequestDTO.class));
    }

    @Test
    void login_whenCorrectCredentials_thenReturnsJwtInHeader() throws Exception {
        String fakeJwt = "abc.def.ghi";
        when(customerService.generateJWTToken("u","p")).thenReturn(fakeJwt);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"username":"u","password":"p"}
                    """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(ApplicationConstants.JWT_HEADER, fakeJwt));

        verify(customerService).generateJWTToken("u","p");
    }
}