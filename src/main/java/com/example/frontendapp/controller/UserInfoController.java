package com.example.frontendapp.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * REST controller that demonstrates calling Keycloak's UserInfo endpoint using the access token
 * from the current OAuth2 session.
 */
@RestController
@RequestMapping("/api")
public class UserInfoController {

  private final String issuerUri;
  private final RestClient restClient;

  public UserInfoController(
      @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}") String issuerUri) {
    this.issuerUri = issuerUri;
    this.restClient = RestClient.create();
  }

  /** Response object containing user information from Keycloak's UserInfo endpoint. */
  @Data
  public static class UserInfoResponse {
    private String sub;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    private String name;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String email;
  }

  /**
   * Calls Keycloak's UserInfo endpoint using the access token from the current session.
   *
   * <p>This endpoint demonstrates: 1. Extracting the access token from the OAuth2AuthorizedClient
   * 2. Making an authenticated request to Keycloak's UserInfo endpoint 3. Returning the user
   * information as JSON 4. Using DPoP if enabled (automatically adds DPoP proof headers)
   *
   * <p>The @RegisteredOAuth2AuthorizedClient annotation automatically provides the
   * OAuth2AuthorizedClient for the current user's session, which contains the access token needed
   * to call protected Keycloak endpoints.
   *
   * @param authorizedClient the OAuth2 authorized client containing the access token
   * @return UserInfo data from Keycloak
   */
  @GetMapping("/userinfo")
  public UserInfoResponse getUserInfo(
      @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient) {

    // Get the access token from the authorized client
    String accessToken = authorizedClient.getAccessToken().getTokenValue();

    // Construct the UserInfo endpoint URL
    String userInfoEndpoint = issuerUri + "/protocol/openid-connect/userinfo";

    // Call Keycloak's UserInfo endpoint with the access token
    return restClient
        .get()
        .uri(userInfoEndpoint)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .body(UserInfoResponse.class);
  }
}
