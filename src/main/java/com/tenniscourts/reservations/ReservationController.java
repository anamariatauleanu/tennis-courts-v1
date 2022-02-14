package com.tenniscourts.reservations;

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
@RequestMapping("/reservations")
public class ReservationController extends BaseRestController {

    private final ReservationService reservationService;

    @ApiOperation("Books a reservation")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Reservation successfully booked."),
            @ApiResponse(code = 400 , message = "Invalid guestID or/and scheduleID."),
            @ApiResponse(code = 404 , message = "Guest or/and Schedule not Found")
    })
    @PostMapping
    public ResponseEntity<Void> bookReservation(@RequestBody CreateReservationRequestDTO createReservationRequestDTO) {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @ApiOperation("Books a list of reservations")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Reservations successfully booked."),
            @ApiResponse(code = 400 , message = "Invalid guestID or/and scheduleID."),
            @ApiResponse(code = 404 , message = "Guest or/and Schedule not found")
    })
    @PostMapping("/collection")
    public ResponseEntity<List<ReservationDTO>> bookReservations(@RequestBody List<CreateReservationRequestDTO> createReservationRequestDTOList) {
        return ResponseEntity.ok(reservationService.bookListOfReservations(createReservationRequestDTOList));
    }

    @ApiOperation("Finds a reservation by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Reservation successfully found."),
            @ApiResponse(code = 400 , message = "Invalid reservationID."),
            @ApiResponse(code = 404 , message = "Reservation not found")
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> findReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }

    @ApiOperation("Cancels a reservation by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Reservation successfully cancelled."),
            @ApiResponse(code = 400 , message = "Invalid reservationID."),
            @ApiResponse(code = 404 , message = "Reservation not found")
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @ApiOperation("Reschedules a reservation")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Reservation successfully rescheduled."),
            @ApiResponse(code = 400 , message = "Invalid reservationID and/or scheduleID."),
            @ApiResponse(code = 404 , message = "Reservation or/and schedule not Found")
    })
    @PutMapping
    public ResponseEntity<ReservationDTO> rescheduleReservation(@RequestBody RescheduleReservationRequestDTO rescheduleReservationRequestDTO) {
        return ResponseEntity.ok(reservationService.rescheduleReservation(rescheduleReservationRequestDTO));
    }

    @ApiOperation("Returns the list of historical reservations")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "List of reservations returned."),
            @ApiResponse(code = 404 , message = "No reservation found.")
    })
    @GetMapping("/history")
    public ResponseEntity<List<ReservationDTO>> findHistoricalReservations() {
        return ResponseEntity.ok(reservationService.findHistoricalReservations());
    }
}
