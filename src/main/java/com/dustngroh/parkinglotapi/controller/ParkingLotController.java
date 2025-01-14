package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.service.ParkingLotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkinglots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public List<ParkingLot> getAllParkingLots() {
        return parkingLotService.getAllParkingLots();
    }

    @GetMapping("/{name}")
    public ResponseEntity<ParkingLot> getParkingLotByName(@PathVariable String name) {
        return parkingLotService.getParkingLotByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ParkingLot createParkingLot(@RequestBody ParkingLot parkingLot) {
        return parkingLotService.saveParkingLot(parkingLot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable Long id) {
        parkingLotService.deleteParkingLot(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/increment-reserved")
    public ResponseEntity<ParkingLot> incrementReservedSpaces(@PathVariable Long id) {
        try {
            ParkingLot updatedParkingLot = parkingLotService.incrementReservedSpaces(id);
            return ResponseEntity.ok(updatedParkingLot);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

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
