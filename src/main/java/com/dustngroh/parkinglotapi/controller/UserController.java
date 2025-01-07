package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.dto.UserMapper;
import com.dustngroh.parkinglotapi.dto.UserRegistrationDTO;
import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.exception.UserAlreadyExistsException;
import com.dustngroh.parkinglotapi.exception.UserNotFoundException;
import com.dustngroh.parkinglotapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
        try {
            User user = userMapper.toEntity(userRegistrationDTO);
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody String newPassword) {
        try {
            userService.changePassword(id, newPassword);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
