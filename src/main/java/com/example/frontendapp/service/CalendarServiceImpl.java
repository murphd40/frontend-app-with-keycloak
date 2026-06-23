package com.example.frontendapp.service;

import com.example.frontendapp.model.Day;
import com.example.frontendapp.model.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {

  @Override
  public Day today() {
    Calendar calendar = Calendar.getInstance();
    return new Day(
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK),
        calendar.get(Calendar.MONTH),
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK),
        calendar.get(Calendar.YEAR));
  }

  @Override
  public Month thisMonth() {
    Calendar calendar = Calendar.getInstance();

    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
    int year = calendar.get(Calendar.YEAR);

    // Get the number of days in the current month
    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    List<Month.Day> daysOfMonth = new ArrayList<>();

    // Iterate through all days of the month
    for (int day = 1; day <= daysInMonth; day++) {
      calendar.set(Calendar.DAY_OF_MONTH, day);
      String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
      daysOfMonth.add(new Month.Day(day, dayName));
    }

    return new Month(currentDay, month, monthName, year, daysOfMonth);
  }
}
