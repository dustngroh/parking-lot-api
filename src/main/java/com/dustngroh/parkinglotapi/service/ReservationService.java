package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.repository.ParkingLotRepository;
import com.dustngroh.parkinglotapi.repository.ReservationRepository;
import com.dustngroh.parkinglotapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParkingLotRepository parkingLotRepository;
    //private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, ParkingLotRepository parkingLotRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingLotRepository = parkingLotRepository;
        //this.userRepository = userRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByUser(String username) {
        return reservationRepository.findByUser_Username(username);
    }

    public List<Reservation> getReservationsByParkingLotId(Long parkingLotId) {
        return reservationRepository.findByParkingLot_Id(parkingLotId);
    }

    public boolean hasReservation(String username, Long parkingLotId) {
        return reservationRepository.findByUser_UsernameAndParkingLot_Id(username, parkingLotId).isPresent();
    }

    @Transactional
    public Reservation createReservation(User user, Long parkingLotId) {
        // Fetch the parking lot
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        // Check if the user already has a reservation for this parking lot
        if (reservationRepository.findByUser_UsernameAndParkingLot_Id(user.getUsername(), parkingLotId).isPresent()) {
            throw new IllegalStateException("User already has a reservation for this parking lot.");
        }

        // Check if there are available spaces
        if (parkingLot.getReservedSpaces() >= parkingLot.getTotalSpaces()) {
            throw new IllegalStateException("No available spaces in this parking lot.");
        }

        // Create a new reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setParkingLot(parkingLot);

        // Increment reserved spaces
        parkingLot.setReservedSpaces(parkingLot.getReservedSpaces() + 1);
        parkingLotRepository.save(parkingLot);

        return reservationRepository.save(reservation);
    }

    // Deletes the Reservation and decrements reserved spaces of the lot
    @Transactional
    public boolean cancelReservation(String username, Long parkingLotId) {
        // Find the user's reservation
        Optional<Reservation> reservationOpt = reservationRepository.findByUser_UsernameAndParkingLot_Id(username, parkingLotId);

        if (reservationOpt.isEmpty()) {
            return false; // No reservation to cancel
        }

        Reservation reservation = reservationOpt.get();
        ParkingLot parkingLot = reservation.getParkingLot();

        // Decrement reserved spaces
        if (parkingLot.getReservedSpaces() > 0) {
            parkingLot.setReservedSpaces(parkingLot.getReservedSpaces() - 1);
            parkingLotRepository.save(parkingLot);
        }

        // Delete the reservation
        reservationRepository.delete(reservation);
        return true;
    }

    // Deletes the Reservation without changing reserved spaces of the lot
    public boolean confirmReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);

        if (reservationOpt.isPresent()) {
            reservationRepository.deleteById(reservationId);
            return true;
        }

        return false;
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
