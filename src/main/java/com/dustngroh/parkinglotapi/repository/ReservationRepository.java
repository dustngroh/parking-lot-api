package com.dustngroh.parkinglotapi.repository;

import com.dustngroh.parkinglotapi.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_Username(String username);
    List<Reservation> findByParkingLot_Name(String parkingLotName);
    List<Reservation> findByParkingLot_Id(Long parkingLotId);
    Optional<Reservation> findByUser_UsernameAndParkingLot_Name(String username, String parkingLotName);
    Optional<Reservation> findByUser_UsernameAndParkingLot_Id(String username, Long parkingLotId);
}