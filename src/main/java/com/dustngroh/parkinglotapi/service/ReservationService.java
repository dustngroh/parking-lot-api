package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.repository.ParkingLotRepository;
import com.dustngroh.parkinglotapi.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParkingLotRepository parkingLotRepository;

    public ReservationService(ReservationRepository reservationRepository, ParkingLotRepository parkingLotRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByUser(String username) {
        return reservationRepository.findByUser_Username(username);
    }

    public List<Reservation> getReservationsByParkingLot(String parkingLotName) {
        return reservationRepository.findByParkingLot_Name(parkingLotName);
    }

    public boolean hasReservation(String username, String parkingLotName) {
        return reservationRepository.findByUser_UsernameAndParkingLot_Name(username, parkingLotName).isPresent();
    }

    public Reservation saveReservation(Reservation reservation) {
        // Fetch the parking lot
        ParkingLot parkingLot = parkingLotRepository.findById(reservation.getParkingLot().getId())
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        // Check if reservedSpaces + 1 exceeds totalSpaces
        if (parkingLot.getReservedSpaces() >= parkingLot.getTotalSpaces()) {
            throw new IllegalStateException("No more spaces can be reserved");
        }

        // Increment reserved spaces
        parkingLot.setReservedSpaces(parkingLot.getReservedSpaces() + 1);
        parkingLotRepository.save(parkingLot);

        // Save the reservation
        return reservationRepository.save(reservation);
    }

    public void confirmReservation(Long reservationId) {
        // Fetch the reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Simply delete the reservation (reservedSpaces remain unchanged)
        reservationRepository.delete(reservation);
    }

    public void cancelReservation(Long reservationId) {
        // Fetch the reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Fetch the parking lot associated with the reservation
        ParkingLot parkingLot = reservation.getParkingLot();
        if (parkingLot == null) {
            throw new IllegalStateException("Reservation is not associated with any parking lot");
        }

        // Decrement reservedSpaces
        if (parkingLot.getReservedSpaces() > 0) {
            parkingLot.setReservedSpaces(parkingLot.getReservedSpaces() - 1);
            parkingLotRepository.save(parkingLot);
        } else {
            throw new IllegalStateException("Reserved spaces are already at minimum");
        }

        // Delete the reservation
        reservationRepository.delete(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
