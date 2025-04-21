package com.sunnyhsu.springboottravelproject.controller;

import com.sunnyhsu.springboottravelproject.dto.UserLoginRequest;
import com.sunnyhsu.springboottravelproject.dto.UserRegisterRequest;
import com.sunnyhsu.springboottravelproject.model.User;
import com.sunnyhsu.springboottravelproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
public class UserController {

    Logger log = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // 註冊
    @PostMapping("/users/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest){
//        String password = userRegisterRequest.getPassword();
//        String confirmPassword = userRegisterRequest.getConfirmPassword();
//
//        // 檢查密碼一致性
//        if (!password.equals(confirmPassword)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("密碼不一致");
//        }

        Integer userId = userService.register(userRegisterRequest);

        User user = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body("註冊成功");
    }

    // 登入
    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userLoginRequest,
                                   HttpSession session,
                                   HttpServletRequest request
    ){

        User user = userService.login(userLoginRequest);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
        }

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

        String redirect = request.getParameter("redirect");

        return ResponseEntity.ok(Map.of("redirect", redirect != null ? redirect : "/destinations"));
    }

    // 上傳大頭貼
    @PostMapping("/users/{userId}/avatar")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Integer userId,
            @RequestParam("avatarFile") MultipartFile avatarFile,
            HttpSession session
    ) {

        // 1) 檢查 Session 中的 user
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || !sessionUser.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限上傳他人大頭照");
        }

        // 2) 檢查檔案是否存在
        if (avatarFile.isEmpty()) {
            return ResponseEntity.badRequest().body("未選擇檔案");
        }

        // 3) 決定要儲存檔案在哪裡
        String uploadsDir = "uploads/";
        File dir = new File(uploadsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4) 檔名 + 副檔名
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(avatarFile.getOriginalFilename()));
        String newFilename = "user_" + userId + "_" + originalFilename;

        // 5) 將檔案存到 uploads/ 目錄
        Path filePath = Paths.get(uploadsDir, newFilename);
        try {
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("上傳檔案失敗：" + e.getMessage());
        }

        // 6) 將檔案路徑更新到資料庫 avatar 欄位
        String fileRelativePath = "/" + uploadsDir + newFilename;
        userService.updateUserAvatar(userId, fileRelativePath);

        return ResponseEntity.ok("上傳成功");
    }
}

