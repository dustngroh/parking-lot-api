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
import java.util.Map;
import java.util.Optional;

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

    /**
     * Get all reservations (Admin Use)
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    /**
     * Get reservations for the logged-in user
     */
    @GetMapping("/user")
    public ResponseEntity<?> getReservationsByUser(
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
            }

            List<Reservation> reservations = reservationService.getReservationsByUser(username);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/parkinglot/{parkingLotId}")
    public ResponseEntity<List<Reservation>> getReservationsByParkingLot(
            @PathVariable Long parkingLotId,
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isEmpty() || !userOpt.get().getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<Reservation> reservations = reservationService.getReservationsByParkingLotId(parkingLotId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Check if the logged-in user has a reservation in a parking lot
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> hasReservation(
            @RequestParam Long parkingLotId,
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("hasReservation", false));
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("hasReservation", false));
            }

            boolean exists = reservationService.hasReservation(username, parkingLotId);
            return ResponseEntity.ok(Map.of("hasReservation", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("hasReservation", false));
        }
    }

    /**
     * Create a new reservation
     */
    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestParam Long parkingLotId,
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
            }

            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }

            Reservation newReservation = reservationService.createReservation(userOpt.get(), parkingLotId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Cancel a reservation
     */
    @DeleteMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancelReservation(
            @RequestParam Long parkingLotId,
            @CookieValue(name = "jwtToken", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
            }

            boolean canceled = reservationService.cancelReservation(username, parkingLotId);
            if (!canceled) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "No reservation found to cancel."));
            }

            return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Confirm a reservation by ID
     * Deletes the reservation without changing reserved spaces of the associated parking lot
     */
    @DeleteMapping("/{id}/confirm")
    public ResponseEntity<Map<String, String>> confirmReservation(@PathVariable Long id) {
        boolean confirmed = reservationService.confirmReservation(id);

        if (!confirmed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Reservation not found."));
        }

        return ResponseEntity.ok(Map.of("message", "Reservation confirmed successfully."));
    }

    /**
     * Delete a reservation by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(Map.of("message", "Reservation deleted successfully."));
    }
}
