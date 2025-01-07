package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    public void testGetUserByUsername() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        user.setRole("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        Optional<User> retrievedUser = userService.getUserByUsername("admin");

        assertEquals("admin", retrievedUser.orElseThrow().getUsername());
        assertEquals("ADMIN", retrievedUser.orElseThrow().getRole());
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setRole("USER");

        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertEquals("john_doe", savedUser.getUsername());
        assertEquals("USER", savedUser.getRole());
    }
}
