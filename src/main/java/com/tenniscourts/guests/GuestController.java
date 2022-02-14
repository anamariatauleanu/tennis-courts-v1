package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("guests")
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @ApiOperation("Adds a guest")
    @ApiResponses(value = {
            @ApiResponse(code = 201 , message = "Guest successfully created."),
    })
    @PostMapping
    public ResponseEntity<Void> addGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.addGuest(guestDTO).getId())).build();
    }


    @ApiOperation("Finds a guest by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Guest successfully found."),
            @ApiResponse(code = 400 , message = "Invalid guestID."),
            @ApiResponse(code = 404 , message = "Guest not found")
    })
    @GetMapping("/{guestId}")
    public ResponseEntity<GuestDTO> findGuestById(@PathVariable Long guestId) {
        return ResponseEntity.ok(guestService.findGuest(guestId));
    }

    @ApiOperation("Finds all guests with the possibility to search by name")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "List of guests returned."),
            @ApiResponse(code = 404 , message = "No guest found.")
    })
    @GetMapping
    public ResponseEntity<List<GuestDTO>> findGuests(@RequestParam (required = false) String name) {
        return ResponseEntity.ok(guestService.findGuests(name));
    }

    @ApiOperation(value = "Delete a guest")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Guest successfully deleted."),
            @ApiResponse(code = 400 , message = "Invalid guestID."),
            @ApiResponse(code = 404 , message = "Guest not found")
    })
    @DeleteMapping("/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Updates a guest")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Guest successfully updated."),
            @ApiResponse(code = 400 , message = "Invalid guestID."),
            @ApiResponse(code = 404 , message = "Guest not found")
    })
    @PutMapping
    public ResponseEntity<GuestDTO> updateGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.updateGuest(guestDTO));
    }
}
