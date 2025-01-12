package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.dto.UserRegistrationDTO;
import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.exception.UserAlreadyExistsException;
import com.dustngroh.parkinglotapi.exception.UserNotFoundException;
import com.dustngroh.parkinglotapi.service.UserService;
import com.dustngroh.parkinglotapi.dto.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class, UserControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = false) // Disable Spring Security filters
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private UserRegistrationDTO validUserDTO;
    private User validUser;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userMapper, userService);

        validUserDTO = new UserRegistrationDTO();
        validUserDTO.setUsername("john_doe");
        validUserDTO.setPassword("securepassword123");
        validUserDTO.setFirstName("John");
        validUserDTO.setLastName("Doe");
        validUserDTO.setPlateNumber("ABC123");
        validUserDTO.setRole("USER");

        validUser = new User();
        validUser.setUsername("john_doe");
        validUser.setPassword("securepassword123");
        validUser.setFirstName("John");
        validUser.setLastName("Doe");
        validUser.setPlateNumber("ABC123");
        validUser.setRole("USER");
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        when(userMapper.toEntity(validUserDTO)).thenReturn(validUser);
        // No exception thrown, implying the user was successfully registered
        //doNothing().when(userService).registerUser(validUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User created successfully."));

        verify(userMapper, times(1)).toEntity(validUserDTO);
        verify(userService, times(1)).registerUser(validUser);
    }

    @Test
    public void testRegisterUser_Conflict() throws Exception {
        when(userMapper.toEntity(validUserDTO)).thenReturn(validUser);
        // Mock exception for a conflict scenario
        doThrow(new UserAlreadyExistsException("User with username john_doe already exists."))
                .when(userService).registerUser(validUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with username john_doe already exists."));

        verify(userMapper, times(1)).toEntity(validUserDTO);
        verify(userService, times(1)).registerUser(validUser);
    }

    @Test
    public void testLogin_Success() throws Exception {
        String username = "john_doe";
        String password = "securepassword123";

        User validUser = new User();
        validUser.setUsername(username);
        validUser.setPassword(password); // Assume this is hashed in the actual implementation

        // Mock the service to return the user for correct credentials
        when(userService.authenticate(username, password)).thenReturn(validUser);

        // Create the request payload
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", password
        ));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful. Token: sample-jwt-token"));

        verify(userService, times(1)).authenticate(username, password);
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        String username = "john_doe";
        String password = "wrongpassword";

        // Mock the service to throw an exception for invalid credentials
        when(userService.authenticate(username, password))
                .thenThrow(new UserNotFoundException("Invalid username or password"));

        // Create the request payload
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", password
        ));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        verify(userService, times(1)).authenticate(username, password);
    }

    @Test
    public void testLogin_MissingFields() throws Exception {
        // Missing password field in request
        String incompleteLoginRequest = objectMapper.writeValueAsString(Map.of(
                "username", "john_doe"
        ));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompleteLoginRequest))
                .andExpect(status().isBadRequest());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        public UserMapper userMapper() {
            return mock(UserMapper.class);
        }
    }
}
