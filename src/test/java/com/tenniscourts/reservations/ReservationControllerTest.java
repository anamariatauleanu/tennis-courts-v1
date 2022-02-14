package com.tenniscourts.reservations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.common.BaseTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToObject;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationControllerTest extends BaseTest {

    @Test
    public void testBookReservationCreated() throws Exception {
        testBookReservation(1L, 2L, status().isCreated());
    }

    @Test
    public void testBookReservationGuestNotFound() throws Exception {
        testBookReservation(10L, 2L, status().isNotFound());
    }

    @Test
    public void testBookReservationScheduleNotFound() throws Exception {
        testBookReservation(1L, 22L, status().isNotFound());
    }

    @Test
    public void testBookReservationScheduleStartDateNotValid() throws Exception {
        testBookReservation(1L, 1L, status().isBadRequest());
    }

    @Test
    public void testBookReservationAlreadyExists() throws Exception {
        testBookReservation(2L, 4L, status().isConflict());
    }

    @Test
    public void testBookListOfReservations() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO(1L, 5L);
        CreateReservationRequestDTO createReservationRequestDTO1 = new CreateReservationRequestDTO(1L, 7L);
        mockMvc.perform(post("/reservations/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Arrays.asList(createReservationRequestDTO1, createReservationRequestDTO))))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testFindReservationById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/reservations/1")).andExpect(status().isOk()).andReturn();
        ReservationDTO reservationDTO = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ReservationDTO.class);

        assertNotNull(reservationDTO);
        assertThat(reservationDTO.getId(), equalTo(1L));
    }

    @Test
    public void testFindReservationByIdNotFound() throws Exception {
        mockMvc.perform(get("/reservations/111")).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void testCancelReservation() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/reservations/2")).andExpect(status().isOk()).andReturn();
        ReservationDTO reservationDTO = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ReservationDTO.class);

        assertNotNull(reservationDTO);
        assertThat(reservationDTO.getReservationStatus(), equalToObject(ReservationStatus.CANCELLED.name()));
    }

    @Test
    public void testRescheduleReservation() throws Exception {
        RescheduleReservationRequestDTO rescheduleReservationRequestDTO = new RescheduleReservationRequestDTO(1L, 6L);
        MvcResult mvcResult = mockMvc.perform(put("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rescheduleReservationRequestDTO)))
                .andExpect(status().isOk()).andReturn();
        ReservationDTO reservationDTO = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ReservationDTO.class);

        assertNotNull(reservationDTO);
        assertThat(reservationDTO.getSchedule().getId(), equalTo(6L));
    }

    @Test
    public void testReservationsHistory() throws Exception {
        mockMvc.perform(get("/reservations/history")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

    private void testBookReservation(Long guestId, Long scheduleId, ResultMatcher resultMatcher) throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO(guestId, scheduleId);
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createReservationRequestDTO)))
                .andExpect(resultMatcher).andReturn();
    }
}
