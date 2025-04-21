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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/auth/google")
public class GoogleOAuthController {

    @Autowired
    private UserService userService;

    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String googleRedirectUri;

    private final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_USER_URL  = "https://www.googleapis.com/oauth2/v2/userinfo";

    @GetMapping("/buildAuthUrl")
    public String buildAuthUrl(HttpSession session) {
        return UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTH_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", googleClientId)
                .queryParam("scope", "profile email openid")
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("access_type", "offline")
                .toUriString();
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam String code,
                                         HttpSession session)
    {
        // 交換 token
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", googleClientId);
        form.add("client_secret", googleClientSecret);
        form.add("code", code);
        form.add("redirect_uri", googleRedirectUri);
        form.add("grant_type", "authorization_code");

        ResponseEntity<Map> tokenResp = restTemplate.postForEntity(GOOGLE_TOKEN_URL, new HttpEntity<>(form, headers), Map.class);

        String accessToken = (String) tokenResp.getBody().get("access_token");

        // 取得 user info
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<?> userEntity = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResponse = restTemplate.exchange(GOOGLE_USER_URL, HttpMethod.GET, userEntity, Map.class);

        Map userBody = userResponse.getBody();
        String email = (String) userBody.get("email");
        String name  = (String) userBody.get("name");
        String avatar = (String) userBody.get("picture");

        userService.upsertUserForOAuth(email, name, avatar);
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
