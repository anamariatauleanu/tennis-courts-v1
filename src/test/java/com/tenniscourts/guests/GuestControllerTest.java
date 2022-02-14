package com.tenniscourts.guests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.common.BaseTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GuestControllerTest extends BaseTest {

    @Test
    public void testAddGuest() throws Exception {
        GuestDTO guestDTO = new GuestDTO(null, "Test");
        mockMvc.perform(post("/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(guestDTO)))
                .andExpect(status().isCreated()).andReturn();
    }

    @Test
    public void testGetAllGuests() throws Exception {
        mockMvc.perform(get("/guests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void findGuestById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/guests/1")).andExpect(status().isOk()).andReturn();
        GuestDTO guestDTO = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), GuestDTO.class);

        assertNotNull(guestDTO);
        assertThat(guestDTO.getId(), equalTo(1L));
    }

    @Test
    public void testFindGuestByIdNotFound() throws Exception {
        mockMvc.perform(get("/guests/111")).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void testFindGuestsByName() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/guests?name=Rafael"))
                .andExpect(status().isOk()).andReturn();
        List<GuestDTO> result = Arrays.asList(new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), GuestDTO[].class));

        assertFalse(result.isEmpty());
        assertThat(result.get(0).getName(), Matchers.containsString("Rafael"));
    }

    @Test
    public void testUpdateGuest() throws Exception {
        GuestDTO guestDTO = new GuestDTO(1L, "Gigi");
        MvcResult mvcResult = mockMvc.perform(put("/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(guestDTO)))
                .andExpect(status().isOk()).andReturn();
        GuestDTO result = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), GuestDTO.class);

        assertNotNull(result);
        assertThat(result.getName(), equalTo("Gigi"));
    }

    @Test
    public void testDeleteGuest() throws Exception {
        mockMvc.perform(delete("/guests/3")).andExpect(status().isOk()).andReturn();
    }


}
