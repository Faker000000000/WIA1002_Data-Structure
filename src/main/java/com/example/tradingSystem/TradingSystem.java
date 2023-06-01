package com.example.tradingSystem;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradingSystem {


    private int lotPoolSize;
    private LocalDateTime competitionEndDate;


    public TradingSystem(LocalDateTime competitionStartDate) {

        lotPoolSize = 500;
        competitionEndDate = competitionStartDate.plusWeeks(6); // The competition runs for 6 weeks for the current time that starts the program.
    }

    private boolean isWithinTradingHours() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        LocalTime time = now.toLocalTime();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false; // Trading is closed on weekends
        }

        LocalTime morningSessionStartTime = LocalTime.of(9, 0);
        LocalTime morningSessionEndTime = LocalTime.of(12, 30);
        LocalTime afternoonSessionStartTime = LocalTime.of(14, 30);
        LocalTime afternoonSessionEndTime = LocalTime.of(17, 0);

        if ((time.isAfter(morningSessionStartTime) && time.isBefore(morningSessionEndTime))
                || (time.isAfter(afternoonSessionStartTime) && time.isBefore(afternoonSessionEndTime))) {
            return true; // Trading is open during regular market hours
        }

        return false;
    }

    // Check if the period is within 3 days.
    private boolean isWithinInitialTradingPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(competitionEndDate.minusDays(3));
    }

}
