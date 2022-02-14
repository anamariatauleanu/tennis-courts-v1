package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("schedules")
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @ApiOperation("Schedules an appointment for tennis court")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Schedule successfully created."),
            @ApiResponse(code = 400 , message = "Invalid tennisCourtID or startDate."),
            @ApiResponse(code = 404 , message = "Tennis court not found")
    })
    @PostMapping
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO).getId())).build();
    }

    @ApiOperation("Searches schedules between two given dates with the possibility to check containing only available items")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "List of schedules successfully returned."),
            @ApiResponse(code = 400 , message = "Invalid start or end date."),
            @ApiResponse(code = 404 , message = "No schedules found")
    })
    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> findSchedules(@RequestParam("startDate")
                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                           @RequestParam("endDate")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                           @RequestParam(required = false, defaultValue = "false") boolean available) {
        return ResponseEntity.ok(scheduleService.findSchedules(startDate, endDate, available));
    }

    @ApiOperation("Finds a schedule by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Schedule successfully returned."),
            @ApiResponse(code = 400 , message = "Invalid scheduleID."),
            @ApiResponse(code = 404 , message = "Schedule not found")
    })
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }

}
