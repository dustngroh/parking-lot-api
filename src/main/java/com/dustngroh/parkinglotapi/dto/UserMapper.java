package com.dustngroh.parkinglotapi.dto;

import com.dustngroh.parkinglotapi.dto.UserRegistrationDTO;
import com.dustngroh.parkinglotapi.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRegistrationDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setPlateNumber(dto.getPlateNumber());
        user.setRole(dto.getRole());
        return user;
    }

    public UserRegistrationDTO toDTO(User user) {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername(user.getUsername());
        dto.setPlateNumber(user.getPlateNumber());
        dto.setRole(user.getRole());
        return dto;
    }
}