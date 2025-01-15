package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.repository.ParkingLotRepository;
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

    private ParkingLotRepository parkingLotRepository;

    @BeforeEach
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        parkingLotRepository = mock(ParkingLotRepository.class);
        reservationService = new ReservationService(reservationRepository, parkingLotRepository);

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
        // Create and set up a ParkingLot instance
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        // Create and set up a Reservation instance
        Reservation reservation = new Reservation();
        reservation.setParkingLot(parkingLot); // Assign the ParkingLot to the Reservation

        // Mock repository behavior
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // Call the service method
        Reservation savedReservation = reservationService.saveReservation(reservation);

        // Assertions
        assertEquals(parkingLot, savedReservation.getParkingLot());
        assertEquals(51, parkingLot.getReservedSpaces()); // Verify that reserved spaces were incremented
        verify(parkingLotRepository, times(1)).save(parkingLot);
        verify(reservationRepository, times(1)).save(reservation);
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

    @Test
    public void testSaveReservationIncrementsReservedSpaces() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        Reservation reservation = new Reservation();
        reservation.setParkingLot(parkingLot);

        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        reservationService.saveReservation(reservation);

        assertEquals(51, parkingLot.getReservedSpaces());
        verify(parkingLotRepository, times(1)).save(parkingLot);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    public void testConfirmReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.confirmReservation(1L);

        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    public void testCancelReservation() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setParkingLot(parkingLot);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);

        reservationService.cancelReservation(1L);

        assertEquals(49, parkingLot.getReservedSpaces());
        verify(parkingLotRepository, times(1)).save(parkingLot);
        verify(reservationRepository, times(1)).delete(reservation);
    }

}
