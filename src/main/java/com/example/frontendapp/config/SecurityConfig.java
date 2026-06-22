package com.example.frontendapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    /**
     * Configures the security filter chain for OAuth2 Login with Keycloak.
     * Allows public access to home page and error pages, requires authentication for all other requests.
     * Configures OAuth2 login with custom authorities mapper and Keycloak logout handler.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/public/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/home", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(grantedAuthoritiesMapper())
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(keycloakLogoutHandler())
                        .logoutSuccessUrl("/")
                )
                .build();
    }

    /**
     * Maps Keycloak roles from the ID token to Spring Security authorities.
     * Extracts both realm roles (from realm_access.roles) and client roles (from resource_access.<client>.roles).
     * Preserves existing authorities (OIDC_USER, SCOPE_*) while adding Keycloak roles.
     *
     * @return GrantedAuthoritiesMapper that converts Keycloak roles to Spring Security authorities
     */
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            
            authorities.forEach(authority -> {
                // Keep existing authorities (OIDC_USER, SCOPE_*, etc.)
                mappedAuthorities.add(authority);
                
                // Extract Keycloak roles from OIDC ID token
                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    Map<String, Object> claims = oidcUserAuthority.getIdToken().getClaims();
                    
                    // Extract realm roles from realm_access.roles
                    extractRealmRoles(claims, mappedAuthorities);
                    
                    // Extract client roles from resource_access.<client>.roles
                    extractClientRoles(claims, mappedAuthorities);
                }
            });
            
            return mappedAuthorities;
        };
    }
    
    /**
     * Extracts realm-level roles from Keycloak token claims.
     * Looks for roles in the realm_access.roles claim and adds them as authorities.
     *
     * @param claims the token claims map
     * @param mappedAuthorities the set to add extracted authorities to
     */
    @SuppressWarnings("unchecked")
    private void extractRealmRoles(Map<String, Object> claims, Set<GrantedAuthority> mappedAuthorities) {
        // Keycloak stores realm roles in realm_access.roles
        Object realmAccess = claims.get("realm_access");
        if (realmAccess instanceof Map) {
            Map<String, Object> realmAccessMap = (Map<String, Object>) realmAccess;
            Object roles = realmAccessMap.get("roles");
            if (roles instanceof Collection) {
                Collection<String> realmRoles = (Collection<String>) roles;
                realmRoles.forEach(role ->
                    mappedAuthorities.add(new SimpleGrantedAuthority(role))
                );
            }
        }
    }
    
    /**
     * Extracts client-specific roles from Keycloak token claims.
     * Looks for roles in the resource_access.<client-id>.roles claim and adds them as authorities.
     * Iterates through all clients to extract roles from each.
     *
     * @param claims the token claims map
     * @param mappedAuthorities the set to add extracted authorities to
     */
    @SuppressWarnings("unchecked")
    private void extractClientRoles(Map<String, Object> claims, Set<GrantedAuthority> mappedAuthorities) {
        // Keycloak stores client roles in resource_access.<client-id>.roles
        Object resourceAccess = claims.get("resource_access");
        if (resourceAccess instanceof Map) {
            Map<String, Object> resourceAccessMap = (Map<String, Object>) resourceAccess;
            
            // Iterate through all clients
            resourceAccessMap.forEach((client, clientAccess) -> {
                if (clientAccess instanceof Map) {
                    Map<String, Object> clientAccessMap = (Map<String, Object>) clientAccess;
                    Object roles = clientAccessMap.get("roles");
                    if (roles instanceof Collection) {
                        Collection<String> clientRoles = (Collection<String>) roles;
                        clientRoles.forEach(role ->
                            mappedAuthorities.add(new SimpleGrantedAuthority(role))
                        );
                    }
                }
            });
        }
    }

    /**
     * Creates a logout handler that performs proper logout from Keycloak.
     * Redirects to Keycloak's logout endpoint with the ID token hint to end the SSO session.
     * After Keycloak logout, redirects back to the application's base URL.
     *
     * @return LogoutHandler that handles Keycloak logout
     */
    private LogoutHandler keycloakLogoutHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
                try {
                    OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    
                    String logoutUrl = UriComponentsBuilder
                            .fromUriString(issuerUri + "/protocol/openid-connect/logout")
                            .queryParam("id_token_hint", oidcUser.getIdToken().getTokenValue())
                            .queryParam("post_logout_redirect_uri", baseUrl)
                            .build()
                            .toUriString();
                    
                    response.sendRedirect(logoutUrl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
