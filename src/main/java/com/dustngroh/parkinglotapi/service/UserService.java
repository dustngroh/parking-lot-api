package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.exception.UserAlreadyExistsException;
import com.dustngroh.parkinglotapi.exception.UserNotFoundException;
import com.dustngroh.parkinglotapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User registerUser(User user) {
        // Check for duplicate username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists.");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // Default role
        }

        // Initialize relationships if needed
        user.setReservations(user.getReservations() == null ? List.of() : user.getReservations());
        user.setParkingLots(user.getParkingLots() == null ? Set.of() : user.getParkingLots());

        return userRepository.save(user);
    }

    public User updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(user -> {
                    // Update fields
                    user.setUsername(updatedUser.getUsername() != null ? updatedUser.getUsername() : user.getUsername());
                    user.setPlateNumber(updatedUser.getPlateNumber() != null ? updatedUser.getPlateNumber() : user.getPlateNumber());
                    user.setRole(updatedUser.getRole() != null ? updatedUser.getRole() : user.getRole());

                    // Update relationships if needed
                    if (updatedUser.getReservations() != null) {
                        user.setReservations(updatedUser.getReservations());
                    }
                    if (updatedUser.getParkingLots() != null) {
                        user.setParkingLots(updatedUser.getParkingLots());
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
    }

    public void changePassword(Long userId, String newPassword) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    String hashedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(hashedPassword);
                    userRepository.save(user);
                }, () -> {
                    throw new UserNotFoundException("User with ID " + userId + " not found.");
                });
    }

}