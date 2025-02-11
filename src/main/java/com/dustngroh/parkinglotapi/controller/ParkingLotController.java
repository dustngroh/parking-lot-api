package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.dto.ParkingLotDTO;
import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.service.ParkingLotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parkinglots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingLotDTO>> getAllParkingLots() {
        List<ParkingLotDTO> lots = parkingLotService.getAllParkingLots()
                .stream()
                .map(ParkingLotDTO::new)
                .toList();
        return ResponseEntity.ok(lots);
    }
    
//    @GetMapping("/{name}")
//    public ResponseEntity<ParkingLot> getParkingLotByName(@PathVariable String name) {
//        return parkingLotService.getParkingLotByName(name)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getParkingLotById(@PathVariable Long id) {
        Optional<ParkingLot> parkingLot = parkingLotService.getParkingLotById(id);
        return parkingLot.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ParkingLot> createParkingLot(@RequestBody ParkingLotDTO parkingLotDTO) {
        ParkingLot createdParkingLot = parkingLotService.createParkingLot(parkingLotDTO);
        return ResponseEntity.ok(createdParkingLot);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable Long id) {
        parkingLotService.deleteParkingLot(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/increment-reserved")
    public ResponseEntity<ParkingLot> incrementReservedSpaces(@PathVariable Long id) {
        try {
            ParkingLot updatedParkingLot = parkingLotService.incrementReservedSpaces(id);
            return ResponseEntity.ok(updatedParkingLot);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/decrement-reserved")
    public ResponseEntity<ParkingLot> decrementReservedSpaces(@PathVariable Long id) {
        try {
            ParkingLot updatedParkingLot = parkingLotService.decrementReservedSpaces(id);
            return ResponseEntity.ok(updatedParkingLot);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
