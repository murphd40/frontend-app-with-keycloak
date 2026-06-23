package com.example.frontendapp.controller;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("token")
public class TokenController {

  @GetMapping("idToken")
  public Map<String, Object> getIdToken(@AuthenticationPrincipal OidcUser oidcUser) {
    return oidcUser.getClaims();
  }
}
