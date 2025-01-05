package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.repository.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ParkingLotServiceTest {

    @Autowired
    private ParkingLotService parkingLotService;

    private ParkingLotRepository parkingLotRepository;

    @BeforeEach
    public void setUp() {
        parkingLotRepository = mock(ParkingLotRepository.class);
        parkingLotService = new ParkingLotService(parkingLotRepository);
    }

    @Test
    public void testCalculateAvailableSpaces() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(40);

        int availableSpaces = parkingLotService.calculateAvailableSpaces(parkingLot);

        assertEquals(60, availableSpaces, "Available spaces should be 60");
    }

    @Test
    public void testGetParkingLotByName() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("Main Lot");

        when(parkingLotRepository.findByName("Main Lot")).thenReturn(Optional.of(parkingLot));

        Optional<ParkingLot> retrievedLot = parkingLotService.getParkingLotByName("Main Lot");

        assertEquals("Main Lot", retrievedLot.orElseThrow().getName());
    }
}