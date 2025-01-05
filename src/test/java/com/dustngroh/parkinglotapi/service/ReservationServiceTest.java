package com.dustngroh.parkinglotapi.service;

import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void testGetReservationsByUser() {
        Reservation reservation = new Reservation();
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(2));

        when(reservationRepository.findByUser_Username("john_doe")).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByUser("john_doe");

        assertEquals(1, reservations.size());
    }

    @Test
    public void testSaveReservation() {
        Reservation reservation = new Reservation();
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(2));

        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation savedReservation = reservationService.saveReservation(reservation);

        assertEquals(reservation.getStartTime(), savedReservation.getStartTime());
        assertEquals(reservation.getEndTime(), savedReservation.getEndTime());
    }
}
