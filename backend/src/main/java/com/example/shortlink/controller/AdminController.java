package com.example.shortlink.controller;

import com.example.shortlink.common.Result;
import com.example.shortlink.dto.CaptchaResponse;
import com.example.shortlink.dto.LoginRequest;
import com.example.shortlink.dto.LoginResponse;
import com.example.shortlink.entity.User;
import com.example.shortlink.service.CaptchaService;
import com.example.shortlink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/captcha")
    public Result<CaptchaResponse> getCaptcha() {
        CaptchaResponse captcha = captchaService.generateCaptcha();
        return Result.success(captcha);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request,
                                        @RequestParam(required = false) String captchaId,
                                        @RequestParam(required = false) String captchaCode) {
        if (captchaId == null || captchaCode == null || captchaCode.isEmpty()) {
            return Result.error(400, "验证码不能为空");
        }

        if (!captchaService.validateCaptcha(captchaId, captchaCode)) {
            return Result.error(400, "验证码错误");
        }

        LoginResponse response = userService.login(request);
        if (response == null) {
            return Result.error(401, "Invalid username or password");
        }
        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userService.logout(token);
        return Result.success();
    }

    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @PutMapping("/user/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    @GetMapping("/user/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = null;

        String tokenKey = "user:token:" + token;
        try {
            Object id = redisTemplate.opsForValue().get(tokenKey);
            if (id != null) {
                userId = Long.parseLong(id.toString());
            }
        } catch (Exception e) {
            System.out.println("Redis get failed, using database fallback: " + e.getMessage());
        }

        User user = null;
        if (userId == null) {
            user = userService.getUserByUsernameFromSession(token);
            if (user != null) {
                userId = user.getId();
            }
        } else {
            user = userService.getUserById(userId);
        }

        if (userId == null) {
            return Result.error(401, "Invalid token");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("isAdmin", user.getIsAdmin());
        return Result.success(result);
    }

    @PostMapping("/debug/create-admin")
    public Result<String> createAdminDebug() {
        userService.createUser("admin", "admin123", "admin@example.com");
        return Result.success("Admin user created or already exists");
    }

    @GetMapping("/debug/user/{username}")
    public Result<User> getUserDebug(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error(404, "User not found");
        }
        user.setPassword("[HIDDEN]");
        return Result.success(user);
    }
}