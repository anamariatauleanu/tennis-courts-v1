package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    private final GuestMapper guestMapper;

    public GuestDTO addGuest(GuestDTO guestDTO) {
        return guestMapper.map(guestRepository.saveAndFlush(guestMapper.map(guestDTO)));
    }

    public GuestDTO updateGuest(GuestDTO guestDTO) {
        findGuest(guestDTO.getId());
        return guestMapper.map(guestRepository.saveAndFlush(guestMapper.map(guestDTO)));
    }

    public void deleteGuest(Long id) {
        findGuest(id);
        guestRepository.deleteById(id);
    }

    public GuestDTO findGuest(Long id) {
        return guestRepository.findById(id).map(guestMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Could not find guest with ID " + id);
        });
    }


    public List<GuestDTO> findGuests(String name) {
        if (StringUtils.isEmpty(name)) {
            return guestRepository.findAll().stream().map(guestMapper::map).collect(Collectors.toList());
        }
        return guestRepository.findByNameContainingIgnoreCase(name).stream().map(guestMapper::map).collect(Collectors.toList());
    }
}
