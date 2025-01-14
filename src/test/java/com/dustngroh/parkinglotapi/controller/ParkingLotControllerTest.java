package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.service.ParkingLotService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class ParkingLotControllerTest {


    @Mock
    private ParkingLotService parkingLotService;

    @InjectMocks
    private ParkingLotController parkingLotController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parkingLotController).build();
    }


    @Test
    public void testGetAllParkingLots() throws Exception {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        when(parkingLotService.getAllParkingLots()).thenReturn(List.of(parkingLot));

        mockMvc.perform(get("/api/parkinglots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Main Lot"))
                .andExpect(jsonPath("$[0].totalSpaces").value(100))
                .andExpect(jsonPath("$[0].reservedSpaces").value(50));
    }

    @Test
    public void testGetParkingLotByName() throws Exception {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        when(parkingLotService.getParkingLotByName("Main Lot")).thenReturn(Optional.of(parkingLot));

        mockMvc.perform(get("/api/parkinglots/Main Lot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Main Lot"))
                .andExpect(jsonPath("$.totalSpaces").value(100))
                .andExpect(jsonPath("$.reservedSpaces").value(50));
    }

    @Test
    public void testGetParkingLotByNameNotFound() throws Exception {
        when(parkingLotService.getParkingLotByName("NonExistentLot")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/parkinglots/NonExistentLot"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateParkingLot() throws Exception {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("New Lot");
        parkingLot.setTotalSpaces(150);
        parkingLot.setReservedSpaces(10);

        //when(parkingLotService.saveParkingLot(parkingLot)).thenReturn(parkingLot);
        when(parkingLotService.saveParkingLot(any(ParkingLot.class))).thenReturn(parkingLot);

        mockMvc.perform(post("/api/parkinglots")
                        .contentType("application/json")
                        .content("{\"name\":\"New Lot\",\"totalSpaces\":150,\"reservedSpaces\":10}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Lot"))
                .andExpect(jsonPath("$.totalSpaces").value(150))
                .andExpect(jsonPath("$.reservedSpaces").value(10));
    }

    @Test
    public void testDeleteParkingLot() throws Exception {
        mockMvc.perform(delete("/api/parkinglots/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testIncrementReservedSpaces() throws Exception {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        when(parkingLotService.incrementReservedSpaces(1L)).thenReturn(parkingLot);

        mockMvc.perform(patch("/api/parkinglots/1/increment-reserved").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedSpaces").value(50));
    }

    @Test
    public void testDecrementReservedSpaces() throws Exception {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(49);

        when(parkingLotService.decrementReservedSpaces(1L)).thenReturn(parkingLot);

        mockMvc.perform(patch("/api/parkinglots/1/decrement-reserved").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedSpaces").value(49));
    }

}
