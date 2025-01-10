package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
