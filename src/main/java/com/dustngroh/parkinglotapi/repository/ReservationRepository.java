package com.dustngroh.parkinglotapi.repository;

import com.dustngroh.parkinglotapi.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_Username(String username);
    List<Reservation> findByParkingLot_Name(String parkingLotName);
    List<Reservation> findByStartTimeAfter(LocalDateTime startTime);
}