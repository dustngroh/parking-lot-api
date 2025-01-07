package com.dustngroh.parkinglotapi.config;

import com.dustngroh.parkinglotapi.service.UserService;
import com.dustngroh.parkinglotapi.dto.UserMapper;
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
}
