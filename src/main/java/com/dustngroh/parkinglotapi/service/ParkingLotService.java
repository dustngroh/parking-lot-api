package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.repository.ParkingLotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    public Optional<ParkingLot> getParkingLotByName(String name) {
        return parkingLotRepository.findByName(name);
    }

    public ParkingLot saveParkingLot(ParkingLot parkingLot) {
        return parkingLotRepository.save(parkingLot);
    }

    public void deleteParkingLot(Long id) {
        parkingLotRepository.deleteById(id);
    }

    public int calculateAvailableSpaces(ParkingLot parkingLot) {
        return parkingLot.getTotalSpaces() - parkingLot.getReservedSpaces();
    }
}
