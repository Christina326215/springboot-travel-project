package com.sunnyhsu.springboottravelproject.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WeatherController {

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${weather.api.key}")
    private String weatherApiKey;

    @GetMapping("/weather")
    public ResponseEntity<?> getWeather(@RequestParam(required = false, defaultValue = "Taipei") String location) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", weatherApiUrl, location, weatherApiKey);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("無法獲取天氣資料：" + e.getMessage());
        }
    }

}

