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
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/auth/line")
public class LineOAuthController {

    @Autowired
    private UserService userService;

    @Value("${oauth2.line.client-id}")
    private String lineClientId;

    @Value("${oauth2.line.client-secret}")
    private String lineClientSecret;

    @Value("${oauth2.line.redirect-uri}")
    private String lineRedirectUri;

    private final String LINE_AUTH_URL  = "https://access.line.me/oauth2/v2.1/authorize";
    private final String LINE_TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";
    private final String LINE_USER_URL  = "https://api.line.me/v2/profile";

    @GetMapping("/buildAuthUrl")
    public String buildAuthUrl(HttpSession session) {
        String state = UUID.randomUUID().toString().replace("-", "");
        session.setAttribute("line_oauth_state", state);

        return UriComponentsBuilder.fromHttpUrl(LINE_AUTH_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", lineClientId)
                .queryParam("scope", "profile openid email")
                .queryParam("redirect_uri", lineRedirectUri)
                .queryParam("state", state)
                .queryParam("response_mode", "query")
                .toUriString();
    }

    @RequestMapping(value = "/callback", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Void> callback(@RequestParam(required = false) String code,
                                         @RequestParam(required = false) String state,
                                         HttpSession session)
    {
        String sessionState = (String) session.getAttribute("line_oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 交換 token
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", lineRedirectUri);
        form.add("client_id", lineClientId);
        form.add("client_secret", lineClientSecret);

        ResponseEntity<Map> tokenResp = restTemplate.postForEntity(LINE_TOKEN_URL, new HttpEntity<>(form, headers), Map.class);
        String accessToken = (String) tokenResp.getBody().get("access_token");

        // 取得 user info
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<?> userEntity = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResponse = restTemplate.exchange(LINE_USER_URL, HttpMethod.GET, userEntity, Map.class);

        Map userBody = userResponse.getBody();
        String name   = (String) userBody.get("displayName");
        String userId = (String) userBody.get("userId");
        String pictureUrl = (String) userBody.get("pictureUrl");

        String email = userId + "@line.com";

        userService.upsertUserForOAuth(email, name, pictureUrl);
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
