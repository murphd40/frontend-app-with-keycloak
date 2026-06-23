package com.example.frontendapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.frontendapp.model.Day;
import com.example.frontendapp.model.Month;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test class for {@link CalendarServiceImpl} */
class CalendarServiceImplTest {

  private CalendarServiceImpl calendarService;

  @BeforeEach
  void setUp() {
    calendarService = new CalendarServiceImpl();
  }

  @Test
  void today_shouldReturnCurrentDay() {
    // Given
    Calendar expectedCalendar = Calendar.getInstance();
    int expectedDay = expectedCalendar.get(Calendar.DAY_OF_MONTH);
    String expectedDayName =
        expectedCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
    int expectedMonth = expectedCalendar.get(Calendar.MONTH) + 1;
    String expectedMonthName =
        expectedCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
    int expectedYear = expectedCalendar.get(Calendar.YEAR);

    // When
    Day result = calendarService.today();

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getDay()).isEqualTo(expectedDay);
    assertThat(result.getDayName()).isEqualTo(expectedDayName);
    assertThat(result.getMonth()).isEqualTo(expectedMonth);
    assertThat(result.getMonthName()).isEqualTo(expectedMonthName);
    assertThat(result.getYear()).isEqualTo(expectedYear);
  }

  @Test
  void thisMonth_shouldReturnCurrentMonth() {
    // Given
    Calendar expectedCalendar = Calendar.getInstance();
    int expectedToday = expectedCalendar.get(Calendar.DAY_OF_MONTH);
    int expectedMonth = expectedCalendar.get(Calendar.MONTH) + 1;
    String expectedMonthName =
        expectedCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
    int expectedYear = expectedCalendar.get(Calendar.YEAR);
    int expectedDaysInMonth = expectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    // When
    Month result = calendarService.thisMonth();

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getToday()).isEqualTo(expectedToday);
    assertThat(result.getMonth()).isEqualTo(expectedMonth);
    assertThat(result.getMonthName()).isEqualTo(expectedMonthName);
    assertThat(result.getYear()).isEqualTo(expectedYear);
    assertThat(result.getDays()).hasSize(expectedDaysInMonth);
  }

  @Test
  void thisMonth_shouldReturnAllDaysInSequence() {
    LocalDate now = LocalDate.now();
    YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonth());
    int expectedDaysInMonth = yearMonth.lengthOfMonth();

    // When
    Month result = calendarService.thisMonth();

    // Then
    assertThat(result.getDays()).hasSize(expectedDaysInMonth);
    for (int i = 0; i < result.getDays().size(); i++) {
      assertThat(result.getDays().get(i).getDay()).isEqualTo(i + 1);
    }
  }

  @Test
  void thisMonth_shouldReturnCorrectDayNamesForEachDay() {
    // Given
    Calendar calendar = Calendar.getInstance();
    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    // When
    Month result = calendarService.thisMonth();

    // Then
    for (int i = 0; i < daysInMonth; i++) {
      calendar.set(Calendar.DAY_OF_MONTH, i + 1);
      String expectedDayName =
          calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
      assertThat(result.getDays().get(i).getDayName()).isEqualTo(expectedDayName);
    }
  }

  @Test
  void thisMonth_shouldMatchTodayMethodForCurrentDay() {
    // When
    Day today = calendarService.today();
    Month thisMonth = calendarService.thisMonth();

    // Then
    assertThat(thisMonth.getToday()).isEqualTo(today.getDay());
    assertThat(thisMonth.getMonth()).isEqualTo(today.getMonth());
    assertThat(thisMonth.getMonthName()).isEqualTo(today.getMonthName());
    assertThat(thisMonth.getYear()).isEqualTo(today.getYear());
  }
}
