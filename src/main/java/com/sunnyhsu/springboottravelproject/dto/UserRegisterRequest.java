package com.sunnyhsu.springboottravelproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

//    @NotBlank
//    private String confirmPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getConfirmPassword() {
//        return confirmPassword;
//    }
//
//    public void setConfirmPassword(String confirmPassword) {
//        this.confirmPassword = confirmPassword;
//    }
}
