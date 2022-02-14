package com.tenniscourts.tenniscourts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.common.BaseTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TennisCourtControllerTest extends BaseTest {

    @Test
    public void testAddTennisCourt() throws Exception {
        TennisCourtDTO tennisCourtDTO = new TennisCourtDTO(null, "Test", null);
        mockMvc.perform(post("/tennis-courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tennisCourtDTO)))
                .andExpect(status().isCreated()).andReturn();
    }

    @Test
    public void testFindTennisCourtById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/tennis-courts/1")).andExpect(status().isOk()).andReturn();
        TennisCourtDTO tennisCourtDTO = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), TennisCourtDTO.class);

        assertNotNull(tennisCourtDTO);
        assertThat(tennisCourtDTO.getId(), equalTo(1L));
    }

    @Test
    public void testFindTennisCourtByIdNotFound() throws Exception {
        mockMvc.perform(get("/tennis-courts/111")).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void testFindTennisCourtWithSchedules() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/tennis-courts/1/schedules")).andExpect(status().isOk()).andReturn();
        TennisCourtDTO tennisCourtDTO = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), TennisCourtDTO.class);

        assertNotNull(tennisCourtDTO);
        assertFalse(tennisCourtDTO.getTennisCourtSchedules().isEmpty());
    }
}
