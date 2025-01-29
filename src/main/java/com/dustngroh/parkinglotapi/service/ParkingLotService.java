package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.dto.ParkingLotDTO;
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

    public ParkingLot createParkingLot(ParkingLotDTO parkingLotDTO) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(parkingLotDTO.getName());
        parkingLot.setAddress(parkingLotDTO.getAddress());
        parkingLot.setTotalSpaces(parkingLotDTO.getTotalSpaces());

        // Ensure reservedSpaces does not exceed totalSpaces
        if (parkingLotDTO.getReservedSpaces() > parkingLotDTO.getTotalSpaces()) {
            throw new IllegalArgumentException("Reserved spaces cannot exceed total spaces.");
        }
        parkingLot.setReservedSpaces(parkingLotDTO.getReservedSpaces());

        return parkingLotRepository.save(parkingLot);
    }


    public void deleteParkingLot(Long id) {
        parkingLotRepository.deleteById(id);
    }

    public int calculateAvailableSpaces(ParkingLot parkingLot) {
        return parkingLot.getTotalSpaces() - parkingLot.getReservedSpaces();
    }

    public ParkingLot incrementReservedSpaces(Long parkingLotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        if (parkingLot.getReservedSpaces() < parkingLot.getTotalSpaces()) {
            parkingLot.setReservedSpaces(parkingLot.getReservedSpaces() + 1);
            return parkingLotRepository.save(parkingLot);
        } else {
            throw new IllegalStateException("No available spaces to reserve");
        }
    }

    public ParkingLot decrementReservedSpaces(Long parkingLotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        if (parkingLot.getReservedSpaces() > 0) {
            parkingLot.setReservedSpaces(parkingLot.getReservedSpaces() - 1);
            return parkingLotRepository.save(parkingLot);
        } else {
            throw new IllegalStateException("No reserved spaces to release");
        }
    }
}
