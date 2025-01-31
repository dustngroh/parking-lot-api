package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.ParkingLot;
import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.repository.ParkingLotRepository;
import com.dustngroh.parkinglotapi.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ReservationServiceTest.TestConfig.class})
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private ReservationService reservationService;

    private final String username = "john_doe";
    private final Long parkingLotId = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        reservationService = new ReservationService(reservationRepository, parkingLotRepository);
    }

    @Test
    public void testGetAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation(), new Reservation()));

        List<Reservation> reservations = reservationService.getAllReservations();

        assertEquals(2, reservations.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    public void testGetReservationsByUser() {
        when(reservationRepository.findByUser_Username(username)).thenReturn(List.of(new Reservation()));

        List<Reservation> reservations = reservationService.getReservationsByUser(username);

        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByUser_Username(username);
    }

    @Test
    public void testHasReservation() {
        when(reservationRepository.findByUser_UsernameAndParkingLot_Id(username, parkingLotId))
                .thenReturn(Optional.of(new Reservation()));

        assertTrue(reservationService.hasReservation(username, parkingLotId));
        verify(reservationRepository, times(1)).findByUser_UsernameAndParkingLot_Id(username, parkingLotId);
    }

    @Test
    public void testHasNoReservation() {
        when(reservationRepository.findByUser_UsernameAndParkingLot_Id(username, parkingLotId))
                .thenReturn(Optional.empty());

        assertFalse(reservationService.hasReservation(username, parkingLotId));
        verify(reservationRepository, times(1)).findByUser_UsernameAndParkingLot_Id(username, parkingLotId);
    }

    @Test
    public void testCreateReservation() {
        User mockUser = new User();
        mockUser.setUsername(username);

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(parkingLotId);
        parkingLot.setName("Main Lot");
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        when(parkingLotRepository.findById(parkingLotId)).thenReturn(Optional.of(parkingLot));
        when(reservationRepository.findByUser_UsernameAndParkingLot_Id(username, parkingLotId)).thenReturn(Optional.empty());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation savedReservation = reservationService.createReservation(mockUser, parkingLotId);

        assertEquals(parkingLot, savedReservation.getParkingLot());
        assertEquals(51, parkingLot.getReservedSpaces());
        verify(parkingLotRepository, times(1)).save(parkingLot);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void testCancelReservation() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(parkingLotId);
        parkingLot.setTotalSpaces(100);
        parkingLot.setReservedSpaces(50);

        Reservation reservation = new Reservation();
        reservation.setParkingLot(parkingLot);

        when(reservationRepository.findByUser_UsernameAndParkingLot_Id(username, parkingLotId))
                .thenReturn(Optional.of(reservation));

        boolean result = reservationService.cancelReservation(username, parkingLotId);

        assertTrue(result);
        assertEquals(49, parkingLot.getReservedSpaces());
        verify(parkingLotRepository, times(1)).save(parkingLot);
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    public void testCancelReservationFailsIfNotFound() {
        when(reservationRepository.findByUser_UsernameAndParkingLot_Id(username, parkingLotId))
                .thenReturn(Optional.empty());

        boolean result = reservationService.cancelReservation(username, parkingLotId);

        assertFalse(result);
        verify(reservationRepository, times(1)).findByUser_UsernameAndParkingLot_Id(username, parkingLotId);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    public void testDeleteReservation() {
        doNothing().when(reservationRepository).deleteById(1L);

        reservationService.deleteReservation(1L);

        verify(reservationRepository, times(1)).deleteById(1L);
    }

    @Configuration
    static class TestConfig {
        @Bean
        public ReservationService reservationService(ReservationRepository reservationRepository, ParkingLotRepository parkingLotRepository) {
            return new ReservationService(reservationRepository, parkingLotRepository);
        }

        @Bean
        public ReservationRepository reservationRepository() {
            return mock(ReservationRepository.class);
        }

        @Bean
        public ParkingLotRepository parkingLotRepository() {
            return mock(ParkingLotRepository.class);
        }
    }
}
