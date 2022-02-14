package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("tennis-courts")
public class TennisCourtController extends BaseRestController {

    private final TennisCourtService tennisCourtService;

    @ApiOperation("Adds a tennis court")
    @ApiResponses(value = {
            @ApiResponse(code = 201 , message = "Tennis court successfully created."),
            @ApiResponse(code = 400 , message = "Invalid input data."),
    })
    @PostMapping
    public ResponseEntity<Void> addTennisCourt(@RequestBody TennisCourtDTO tennisCourtDTO) {
        return ResponseEntity.created(locationByEntity(tennisCourtService.addTennisCourt(tennisCourtDTO).getId())).build();
    }

    @ApiOperation("Finds a tennis court by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Tennis court successfully found."),
            @ApiResponse(code = 400 , message = "Invalid tennisCourtID."),
            @ApiResponse(code = 404 , message = "Tennis court not found")
    })
    @GetMapping("/{tennisCourtId}")
    public ResponseEntity<TennisCourtDTO> findTennisCourtById(@PathVariable Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtById(tennisCourtId));
    }

    @ApiOperation("Finds a tennis court with schedules by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Tennis court schedules successfully found."),
            @ApiResponse(code = 400 , message = "Invalid tennisCourtID."),
            @ApiResponse(code = 404 , message = "Tennis court schedules not Found")
    })
    @GetMapping("/{tennisCourtId}/schedules")
    public ResponseEntity<TennisCourtDTO> findTennisCourtWithSchedulesById(@PathVariable Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtWithSchedulesById(tennisCourtId));


    }


}
