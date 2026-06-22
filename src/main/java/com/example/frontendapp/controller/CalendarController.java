package com.example.frontendapp.controller;

import java.util.Calendar;
import java.util.Locale;

import lombok.Value;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("calendar")
@PreAuthorize("hasAuthority('my-role')")
public class CalendarController {

    @Value
    public static class Day {
        int day;
        String dayName;
        int month;
        String monthName;
        int year;
    }

    @GetMapping("today")
    public Day today() {
        Calendar calendar = Calendar.getInstance();

        return new Day(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK),
                calendar.get(Calendar.MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK),
                calendar.get(Calendar.YEAR)
        );
    }

}
