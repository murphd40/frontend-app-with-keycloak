package com.example.frontendapp.controller;

import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

  @Value
  public static class HelloResponse {
    public String message;
  }

  @GetMapping("hello")
  public HelloResponse sayHello() {
    return new HelloResponse("Hello World!");
  }
}
