package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.dto.UserRegistrationDTO;
import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.exception.UserAlreadyExistsException;
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
        validUserDTO.setPlateNumber("ABC123");
        validUserDTO.setRole("USER");

        validUser = new User();
        validUser.setUsername("john_doe");
        validUser.setPassword("securepassword123");
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
