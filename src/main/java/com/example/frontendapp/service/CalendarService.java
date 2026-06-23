package com.example.frontendapp.service;

import com.example.frontendapp.model.Day;
import com.example.frontendapp.model.Month;

public interface CalendarService {

  Day today();

  Month thisMonth();
}
