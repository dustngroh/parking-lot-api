package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.service.ReservationService;
import com.dustngroh.parkinglotapi.service.UserService;
import com.dustngroh.parkinglotapi.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public ReservationController(ReservationService reservationService, UserService userService, JwtUtil jwtUtil) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/user")
    public ResponseEntity<List<Reservation>> getReservationsByUser(
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            List<Reservation> reservations = reservationService.getReservationsByUser(username);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasReservation(
            @RequestParam Long parkingLotId,
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }

            boolean exists = reservationService.hasReservation(username, parkingLotId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestParam Long parkingLotId,
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get the user entity
            User user = userService.getUserByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Create the reservation
            Reservation newReservation = reservationService.createReservation(user, parkingLotId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelReservation(
            @RequestParam Long parkingLotId,
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            boolean canceled = reservationService.cancelReservation(username, parkingLotId);
            if (canceled) {
                return ResponseEntity.ok("Reservation cancelled successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No reservation found to cancel.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
