package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    private ReservationRepository reservationRepository;

    @BeforeEach
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        reservationService = new ReservationService(reservationRepository);
    }

    @Test
    public void testGetAllReservations() {
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        when(reservationRepository.findAll()).thenReturn(List.of(reservation1, reservation2));

        List<Reservation> reservations = reservationService.getAllReservations();

        assertEquals(2, reservations.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    public void testGetReservationsByUser() {
        Reservation reservation = new Reservation();

        when(reservationRepository.findByUser_Username("john_doe")).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByUser("john_doe");

        assertEquals(1, reservations.size());
    }

    @Test
    public void testSaveReservation() {
        Reservation reservation = new Reservation();

        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation savedReservation = reservationService.saveReservation(reservation);

        assertEquals(reservation.getParkingLot(), savedReservation.getParkingLot());
        assertEquals(reservation.getUser(), savedReservation.getUser());
    }

    @Test
    public void testHasReservation() {
        String username = "john_doe";
        String parkingLotName = "Main Lot";

        Reservation reservation = new Reservation();
        when(reservationRepository.findByUser_UsernameAndParkingLot_Name(username, parkingLotName))
                .thenReturn(Optional.of(reservation));

        assertTrue(reservationService.hasReservation(username, parkingLotName));

        verify(reservationRepository, times(1))
                .findByUser_UsernameAndParkingLot_Name(username, parkingLotName);
    }

    @Test
    public void testHasNoReservation() {
        String username = "john_doe";
        String parkingLotName = "NonExistent Lot";

        when(reservationRepository.findByUser_UsernameAndParkingLot_Name(username, parkingLotName))
                .thenReturn(Optional.empty());

        assertFalse(reservationService.hasReservation(username, parkingLotName));

        verify(reservationRepository, times(1))
                .findByUser_UsernameAndParkingLot_Name(username, parkingLotName);
    }
}
