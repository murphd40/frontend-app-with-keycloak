package com.example.frontendapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("debug")
public class DebugController {

    @GetMapping("idToken")
    public Map<String, Object> getIdToken(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            if (oauth2User instanceof OidcUser oidcUser) {
                result.putAll(oidcUser.getClaims());
            } else {
                result.put("error", "Not an OIDC user");
                result.put("attributes", oauth2User.getAttributes());
            }
        } else {
            result.put("error", "Not an OAuth2 authentication");
        }
        
        return result;
    }
    
}
