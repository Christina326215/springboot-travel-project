package com.sunnyhsu.springboottravelproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // 登入 頁面呈現
    @GetMapping("/auth/login")
    public String showLoginPage() {
        return "login";
    }

    // 註冊 頁面呈現
    @GetMapping("/auth/register")
    public String showRegisterPage() {
        return "register";
    }
}
