package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.reservations.ReservationRepository;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tenniscourts.reservations.ReservationStatus.READY_TO_PLAY;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ReservationRepository reservationRepository;

    private final ScheduleRepository scheduleRepository;

    private final TennisCourtRepository tennisCourtRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleDTO addSchedule(CreateScheduleRequestDTO createScheduleRequestDTO) {
        checkCreateInputArgs(createScheduleRequestDTO);
        return getScheduleDTO(createScheduleRequestDTO);
    }

    public List<ScheduleDTO> findSchedules(LocalDate startDate, LocalDate endDate, boolean available) {
        checkSearchInputArgs(startDate, endDate);
        if (available) {
            return findAvailableSchedules(startDate, endDate);
        }
        return findSchedules(startDate, endDate);
    }




    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(scheduleMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Schedule not found.");
        });
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    private List<ScheduleDTO> findAvailableSchedules(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
                .stream()
                .filter(this::hasAvailableSlot)
                .map(scheduleMapper::map)
                .collect(Collectors.toList());
    }

    private List<ScheduleDTO> findSchedules(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
                .stream()
                .map(scheduleMapper::map)
                .collect(Collectors.toList());
    }

    private boolean hasAvailableSlot(Schedule schedule) {
        return reservationRepository.findBySchedule_Id(schedule.getId())
                .stream()
                .anyMatch(r -> !READY_TO_PLAY.equals(r.getReservationStatus()));
    }

    private ScheduleDTO getScheduleDTO(CreateScheduleRequestDTO createScheduleRequestDTO) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findByTennisCourt_IdAndStartDateTime(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO.getStartDateTime());
        if (optionalSchedule.isPresent()) {
            throw new AlreadyExistsEntityException("A schedule with the same data already exists.");
        } else {
            Schedule schedule = scheduleRepository.save(Schedule.builder()
                    .tennisCourt(getTennisCourt(createScheduleRequestDTO))
                    .startDateTime(createScheduleRequestDTO.getStartDateTime())
                    .endDateTime(createScheduleRequestDTO.getStartDateTime().plusHours(1L)).build());
            return scheduleMapper.map(schedule);
        }
    }

    private TennisCourt getTennisCourt(CreateScheduleRequestDTO createScheduleRequestDTO) {
        Optional<TennisCourt> optionalTennisCourt = tennisCourtRepository.findById(createScheduleRequestDTO.getTennisCourtId());
        if (!optionalTennisCourt.isPresent()) {
            throw new EntityNotFoundException("Could not find tennis court with ID " + createScheduleRequestDTO.getTennisCourtId());
        }
        return optionalTennisCourt.get();
    }

    private void checkCreateInputArgs(CreateScheduleRequestDTO createScheduleRequestDTO) {
        if (createScheduleRequestDTO.getStartDateTime() == null || createScheduleRequestDTO.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid start date time.");
        }
    }

    private void checkSearchInputArgs(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Invalid search dates");
        }
    }


}
