package com.example.frontendapp.controller;

import com.example.frontendapp.model.Day;
import com.example.frontendapp.model.Month;
import com.example.frontendapp.security.Require2FA;
import com.example.frontendapp.service.CalendarService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("calendar")
@PreAuthorize("hasAuthority('my-role')")
public class CalendarController {

  private final CalendarService calendarService;

  public CalendarController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @GetMapping("today")
  public Day today() {
    return calendarService.today();
  }

  @Require2FA
  @GetMapping("thisMonth")
  public Month thisMonth() {
    return calendarService.thisMonth();
  }
}
