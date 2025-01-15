package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations(); // Assume this method exists or implement it
    }

    @GetMapping("/user/{username}")
    public List<Reservation> getReservationsByUser(@PathVariable String username) {
        return reservationService.getReservationsByUser(username);
    }

    @GetMapping("/parkinglot/{parkingLotName}")
    public List<Reservation> getReservationsByParkingLot(@PathVariable String parkingLotName) {
        return reservationService.getReservationsByParkingLot(parkingLotName);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasReservation(
            @RequestParam String username,
            @RequestParam String parkingLotName) {
        boolean exists = reservationService.hasReservation(username, parkingLotName);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationService.saveReservation(reservation);
        return ResponseEntity.ok(savedReservation);
    }

    @DeleteMapping("/{id}/confirm")
    public ResponseEntity<String> confirmReservation(@PathVariable Long id) {
        try {
            reservationService.confirmReservation(id);
            return ResponseEntity.ok("Reservation confirmed and deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok("Reservation cancelled and deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
