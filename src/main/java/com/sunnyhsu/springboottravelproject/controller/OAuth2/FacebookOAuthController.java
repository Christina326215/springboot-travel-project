package com.sunnyhsu.springboottravelproject.controller.OAuth2;

import com.sunnyhsu.springboottravelproject.model.User;
import com.sunnyhsu.springboottravelproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/auth/facebook")
public class FacebookOAuthController {

    @Autowired
    private UserService userService;

    private final String FACEBOOK_AUTH_URL = "https://www.facebook.com/v19.0/dialog/oauth";
    private final String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/v19.0/oauth/access_token";
    private final String FACEBOOK_USER_URL  = "https://graph.facebook.com/me?fields=id,name,email";

    @Value("${oauth2.facebook.client-id}")
    private String fbClientId;

    @Value("${oauth2.facebook.client-secret}")
    private String fbClientSecret;

    @Value("${oauth2.facebook.redirect-uri}")
    private String fbRedirectUri;

    @GetMapping("/buildAuthUrl")
    public String buildAuthUrl(HttpSession session) {
        return UriComponentsBuilder.fromHttpUrl(FACEBOOK_AUTH_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", fbClientId)
                .queryParam("scope", "public_profile,email")
                .queryParam("redirect_uri", fbRedirectUri)
                .toUriString();
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam String code, HttpSession session) {

        // 取得 access token
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = UriComponentsBuilder.fromHttpUrl(FACEBOOK_TOKEN_URL)
                .queryParam("client_id", fbClientId)
                .queryParam("client_secret", fbClientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", fbRedirectUri)
                .toUriString();

        Map tokenResp = restTemplate.getForObject(tokenUrl, Map.class);
        String accessToken = (String) tokenResp.get("access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userResp = restTemplate.exchange(FACEBOOK_USER_URL, HttpMethod.GET, entity, Map.class);
        Map userBody = userResp.getBody();

        String email = (String) userBody.get("email");
        String name  = (String) userBody.get("name");

        userService.upsertUserForOAuth(email, name, null);
        User user = userService.getUserByEmail(email);

        // 建立 Spring Security Authentication
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user, // principal
                        null, // credentials
                        Collections.emptyList() // authorities
                );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);

        // 寫入 session
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        session.setAttribute("user", user);

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .location(URI.create("/destinations"))
                .build();
    }
}
