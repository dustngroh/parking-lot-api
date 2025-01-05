package com.dustngroh.parkinglotapi.repository;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    Optional<ParkingLot> findByName(String name);
}