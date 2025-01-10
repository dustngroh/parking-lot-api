package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@ContextConfiguration(classes = {ReservationController.class, ReservationControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    public void setUp() {
        reset(reservationService);
    }

    @Test
    public void testGetAllReservations() throws Exception {
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        when(reservationService.getAllReservations()).thenReturn(List.of(reservation1, reservation2));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    public void testGetReservationsByUser() throws Exception {
        String username = "john_doe";
        Reservation reservation = new Reservation();
        //reservation.setId(1L);
        when(reservationService.getReservationsByUser(username)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations/user/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService, times(1)).getReservationsByUser(username);
    }

    @Test
    public void testGetReservationsByParkingLot() throws Exception {
        String parkingLotName = "Main Lot";
        Reservation reservation = new Reservation();
        when(reservationService.getReservationsByParkingLot(parkingLotName)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations/parkinglot/{parkingLotName}", parkingLotName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService, times(1)).getReservationsByParkingLot(parkingLotName);
    }

    @Test
    public void testHasReservation() throws Exception {
        String username = "john_doe";
        String parkingLotName = "Main Lot";
        when(reservationService.hasReservation(username, parkingLotName)).thenReturn(true);

        mockMvc.perform(get("/api/reservations/exists")
                        .param("username", username)
                        .param("parkingLotName", parkingLotName))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(reservationService, times(1)).hasReservation(username, parkingLotName);
    }

    @Test
    public void testCreateReservation() throws Exception {
        Reservation reservation = new Reservation();
        when(reservationService.saveReservation(any(Reservation.class))).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reservation))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).saveReservation(any(Reservation.class));
    }

    @Test
    public void testDeleteReservation() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/api/reservations/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteReservation(1L);
    }

    @Configuration
    static class TestConfig {
        @Bean
        public ReservationService reservationService() {
            return mock(ReservationService.class);
        }
    }
}
