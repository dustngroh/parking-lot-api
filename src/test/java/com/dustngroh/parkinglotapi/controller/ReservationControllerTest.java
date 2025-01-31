package com.dustngroh.parkinglotapi.controller;

import com.dustngroh.parkinglotapi.entity.Reservation;
import com.dustngroh.parkinglotapi.entity.User;
import com.dustngroh.parkinglotapi.service.ReservationService;
import com.dustngroh.parkinglotapi.service.UserService;
import com.dustngroh.parkinglotapi.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@ContextConfiguration(classes = {ReservationController.class, ReservationControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = false) // Disable security filters in tests
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private final String jwtToken = "mockJwtToken";
    private final String username = "john_doe";
    private final Long parkingLotId = 1L;
    private MockCookie jwtCookie;

    @BeforeEach
    public void setUp() {
        Mockito.reset(reservationService, userService, jwtUtil);

        jwtCookie = new MockCookie("jwtToken", jwtToken);
        jwtCookie.setHttpOnly(true); // Simulate HTTP-only cookie

        when(jwtUtil.getUsernameFromToken(jwtToken)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Optional.of(new User()));
    }

    @Test
    public void testGetReservationsByUser() throws Exception {
        when(reservationService.getReservationsByUser(username)).thenReturn(List.of(new Reservation()));

        mockMvc.perform(get("/api/reservations/user")
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(reservationService, times(1)).getReservationsByUser(username);
    }

    @Test
    public void testHasReservation() throws Exception {
        when(reservationService.hasReservation(username, parkingLotId)).thenReturn(true);

        mockMvc.perform(get("/api/reservations/exists")
                        .param("parkingLotId", String.valueOf(parkingLotId))
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(reservationService, times(1)).hasReservation(username, parkingLotId);
    }

    @Test
    public void testCreateReservation() throws Exception {
        User mockUser = new User();
        Reservation reservation = new Reservation();

        when(userService.getUserByUsername(username)).thenReturn(Optional.of(mockUser));
        when(reservationService.createReservation(mockUser, parkingLotId)).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .param("parkingLotId", String.valueOf(parkingLotId))
                        .cookie(jwtCookie)
                        .with(csrf()))
                .andExpect(status().isCreated());

        verify(reservationService, times(1)).createReservation(mockUser, parkingLotId);
    }

    @Test
    public void testCancelReservation() throws Exception {
        when(reservationService.cancelReservation(username, parkingLotId)).thenReturn(true);

        mockMvc.perform(delete("/api/reservations/cancel")
                        .param("parkingLotId", String.valueOf(parkingLotId))
                        .cookie(jwtCookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Reservation cancelled successfully."));

        verify(reservationService, times(1)).cancelReservation(username, parkingLotId);
    }

    @Test
    public void testCancelReservation_NotFound() throws Exception {
        when(reservationService.cancelReservation(username, parkingLotId)).thenReturn(false);

        mockMvc.perform(delete("/api/reservations/cancel")
                        .param("parkingLotId", String.valueOf(parkingLotId))
                        .cookie(jwtCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No reservation found to cancel."));

        verify(reservationService, times(1)).cancelReservation(username, parkingLotId);
    }

    @Configuration
    static class TestConfig {
        @Bean
        public ReservationService reservationService() {
            return mock(ReservationService.class);
        }

        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return mock(JwtUtil.class);
        }
    }
}
