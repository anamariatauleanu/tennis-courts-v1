package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.tenniscourts.reservations.ReservationStatus.READY_TO_PLAY;
import static com.tenniscourts.reservations.ReservationStatus.RESCHEDULED;

@Service
@AllArgsConstructor
public class ReservationService {

    private final GuestRepository guestRepository;

    private final ScheduleRepository scheduleRepository;

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    private static final BigDecimal RESERVATION_DEPOSIT = BigDecimal.valueOf(10);

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Guest guest = getGuest(createReservationRequestDTO);
        Schedule schedule = getSchedule(createReservationRequestDTO);
        validateSchedule(schedule);
        Reservation reservation = getReservation(guest, schedule);

        return reservationMapper.map(reservationRepository.saveAndFlush(reservation));
    }

    public List<ReservationDTO> bookListOfReservations(List<CreateReservationRequestDTO> createReservationRequestDTOList) {
        return createReservationRequestDTOList.stream().map(this::bookReservation).collect(Collectors.toList());
    }


    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public List<ReservationDTO> findHistoricalReservations() {
        return reservationRepository.findBySchedule_StartDateTimeLessThan(LocalDateTime.now()).stream()
                .map(reservationMapper::map)
                .collect(Collectors.toList());
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {
            this.validateCancellation(reservation);
            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);
        }).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());
        BigDecimal multiplyValue;
        if (hours >= 24) {
            multiplyValue = BigDecimal.ONE;
        } else if (hours >= 12) {
            multiplyValue = BigDecimal.valueOf(0.75);
        } else if (hours >= 2) {
            multiplyValue = BigDecimal.valueOf(0.5);
        } else if (hours > 0) {
            multiplyValue = BigDecimal.valueOf(0.25);
        } else {
            multiplyValue = BigDecimal.ZERO;
        }

        return reservation.getValue().multiply(multiplyValue);
    }

    public ReservationDTO rescheduleReservation(RescheduleReservationRequestDTO rescheduleReservationRequestDTO) {
        Reservation previousReservation = cancel(rescheduleReservationRequestDTO.getReservationId());

        if (rescheduleReservationRequestDTO.getScheduleId().equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(rescheduleReservationRequestDTO.getScheduleId())
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }

    private Reservation getReservation(Guest guest, Schedule schedule) {
        return Reservation.builder()
                .guest(guest)
                .schedule(schedule)
                .value(RESERVATION_DEPOSIT)
                .reservationStatus(READY_TO_PLAY)
                .build();
    }

    private void validateSchedule(Schedule schedule) {
        if (schedule.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reservation starting hour is not valid.");
        }

        if (reservationRepository.findBySchedule_Id(schedule.getId()).stream()
                .anyMatch(r -> READY_TO_PLAY.equals(r.getReservationStatus()))) {
            throw new AlreadyExistsEntityException("Reservation already exists");
        }
    }

    private Schedule getSchedule(CreateReservationRequestDTO createReservationRequestDTO) {
        return scheduleRepository.findById(createReservationRequestDTO.getScheduleId()).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Could not find schedule with ID " + createReservationRequestDTO.getScheduleId());
        });
    }

    private Guest getGuest(CreateReservationRequestDTO createReservationRequestDTO) {
        return guestRepository.findById(createReservationRequestDTO.getGuestId()).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Could not find guest with ID " + createReservationRequestDTO.getGuestId());
        });
    }

}
