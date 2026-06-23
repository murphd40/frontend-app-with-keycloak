package com.example.frontendapp.security;

import java.util.List;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

/**
 * Aspect that enforces 2FA authentication requirement for methods/classes annotated
 * with @Require2FA. Checks the AMR (Authentication Methods References) claim in the OIDC token to
 * verify that OTP was used during authentication.
 */
@Aspect
@Component
public class TwoFactorAuthenticationAspect {

  /**
   * Intercepts methods annotated with @Require2FA and verifies 2FA authentication. Throws
   * AccessDeniedException if the user hasn't authenticated with OTP.
   */
  @Before(
      "@within(com.example.frontendapp.security.Require2FA) || @annotation(com.example.frontendapp.security.Require2FA)")
  public void checkTwoFactorAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AccessDeniedException("User is not authenticated");
    }

    if (!(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
      throw new AccessDeniedException("User is not authenticated via OIDC");
    }

    // Check the AMR (Authentication Methods References) claim
    // Keycloak includes "otp" in the AMR claim when 2FA is used
    Object amrClaim = oidcUser.getClaim("amr");

    boolean hasOtp = false;
    if (amrClaim instanceof List<?> amrList) {
      hasOtp = amrList.contains("otp");
    }

    if (!hasOtp) {
      throw new AccessDeniedException(
          "2FA is required to access this resource. Please configure and use two-factor authentication.");
    }
  }
}
