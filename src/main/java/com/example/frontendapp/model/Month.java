package com.example.frontendapp.model;

import java.util.List;
import lombok.Value;

@Value
public class Month {
  int today;
  int month;
  String monthName;
  int year;
  List<Day> days;

  @Value
  public static class Day {
    int day;
    String dayName;
  }
}
