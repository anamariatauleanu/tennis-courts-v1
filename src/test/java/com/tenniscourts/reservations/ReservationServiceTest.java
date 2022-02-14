package com.tenniscourts.reservations;

import com.tenniscourts.common.BaseTest;
import com.tenniscourts.schedules.Schedule;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertThat;

public class ReservationServiceTest extends BaseTest {

    @InjectMocks
    ReservationService reservationService;

    @Test
    public void getRefundValueFullRefund() {
        buildTest(LocalDateTime.now().plusDays(2), BigDecimal.valueOf(10));
    }

    @Test
    public void getRefundValueSeventyFivePercentRefund() {
        buildTest(LocalDateTime.now().plusHours(13), BigDecimal.valueOf(7.5));
    }

    @Test
    public void getRefundValueFiftyPercentRefund() {
        buildTest(LocalDateTime.now().plusHours(5), BigDecimal.valueOf(5));
    }

    @Test
    public void getRefundValueTwentyFivePercentRefund() {
        buildTest(LocalDateTime.now().plusMinutes(90), BigDecimal.valueOf(2.5));
    }

    @Test
    public void getRefundValueNoRefund() {
        buildTest(LocalDateTime.now(), BigDecimal.ZERO);
    }

    private void buildTest(LocalDateTime startDateTime, BigDecimal value) {
        Schedule schedule = new Schedule();
        schedule.setStartDateTime(startDateTime);
        assertThat(reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(BigDecimal.valueOf(10)).build()), comparesEqualTo(value));
    }
}