package com.dustngroh.parkinglotapi.config;

import com.dustngroh.parkinglotapi.service.ReservationService;
import com.dustngroh.parkinglotapi.service.UserService;
import com.dustngroh.parkinglotapi.dto.UserMapper;
import com.dustngroh.parkinglotapi.util.JwtUtil;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public UserMapper userMapper() {
        return Mockito.mock(UserMapper.class);
    }

    @Bean
    public ReservationService reservationService() {
        return Mockito.mock(ReservationService.class); // Add ReservationService mock
    }

    @Bean
    public JwtUtil jwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }
}
