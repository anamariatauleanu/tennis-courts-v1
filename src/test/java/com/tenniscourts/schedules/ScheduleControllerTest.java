package com.tenniscourts.schedules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.common.BaseTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScheduleControllerTest extends BaseTest {

    @Test
    public void testAddSchedule() throws Exception {
        testAddSchedule(1L, LocalDateTime.now().plusDays(2), status().isCreated());
    }

    @Test
    public void testAddScheduleDateNull() throws Exception {
        testAddSchedule(1L, null, status().isBadRequest());
    }

    @Test
    public void testAddScheduleDateBefore() throws Exception {
        testAddSchedule(1L, LocalDateTime.now().minusDays(2), status().isBadRequest());
    }

    @Test
    public void testAddScheduleAlreadyExists() throws Exception {
        testAddSchedule(2L, LocalDateTime.of(2022, 12, 9, 8, 0), status().isConflict());
    }

    @Test
    public void testSearchSchedules() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/schedules?startDate=" + LocalDate.now().minusYears(3) + "&endDate=" + LocalDate.now().minusDays(4) + "&available=" + Boolean.FALSE))
                .andExpect(status().isOk()).andReturn();
        List<ScheduleDTO> result = Arrays.asList(new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ScheduleDTO[].class));

        assertFalse(result.isEmpty());
    }

    @Test
    public void testSearchSchedulesInvalidInput() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/schedules?startDate=null&endDate=null"))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void testFindScheduleById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/schedules/1")).andExpect(status().isOk()).andReturn();
        ScheduleDTO scheduleDTO = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ScheduleDTO.class);

        assertNotNull(scheduleDTO);
        assertThat(scheduleDTO.getId(), equalTo(1L));
    }

    @Test
    public void testFindScheduleByIdNotFound() throws Exception {
        mockMvc.perform(get("/schedules/111")).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void testFindAvailableSchedules() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/schedules?startDate=" + LocalDate.now() + "&endDate=" + LocalDate.now().plusYears(1) + "&available=" + Boolean.TRUE))
                .andExpect(status().isOk()).andReturn();
        List<ScheduleDTO> result = Arrays.asList(new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ScheduleDTO[].class));

        assertFalse(result.isEmpty());
    }

    private void testAddSchedule(Long tennisCourtId, LocalDateTime startDateTime, ResultMatcher resultMatcher) throws Exception {
        CreateScheduleRequestDTO scheduleRequestDTO = new CreateScheduleRequestDTO(tennisCourtId, startDateTime);
        mockMvc.perform(post("/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(scheduleRequestDTO)))
                .andExpect(resultMatcher).andReturn();
    }
}
