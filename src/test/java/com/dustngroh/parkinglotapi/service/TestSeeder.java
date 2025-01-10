package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.repository.ParkingLotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@SpringBootTest
@Component
@Profile("test")
public class TestSeeder implements CommandLineRunner {
    private final ParkingLotRepository repository;

    public TestSeeder(ParkingLotRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        ParkingLot lot = new ParkingLot();
        lot.setName("Main Lot");
        lot.setTotalSpaces(100);
        repository.save(lot);

        System.out.println("Parking lots: " + repository.findAll());
    }
}
