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
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "Invalid token");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(401, "User not found");
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

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, String> request) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "Invalid token");
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || oldPassword.isEmpty()) {
            return Result.error(400, "原密码不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error(400, "新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return Result.error(400, "新密码长度至少6位");
        }

        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        if (!success) {
            return Result.error(400, "原密码错误");
        }

        return Result.success();
    }

    @PostMapping("/change-username")
    public Result<Void> changeUsername(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, String> request) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "Invalid token");
        }

        String newUsername = request.get("newUsername");

        if (newUsername == null || newUsername.isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (newUsername.length() < 3 || newUsername.length() > 20) {
            return Result.error(400, "用户名长度需在3-20位之间");
        }

        boolean success = userService.changeUsername(userId, newUsername);
        if (!success) {
            return Result.error(400, "用户名已存在");
        }

        return Result.success();
    }

    @PostMapping("/user/{id}/reset-password")
    public Result<Void> resetUserPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error(400, "新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return Result.error(400, "密码长度至少6位");
        }

        boolean success = userService.resetPassword(id, newPassword);
        if (!success) {
            return Result.error(404, "用户不存在");
        }

        return Result.success();
    }

    private Long extractUserIdFromToken(String token) {
        if (token == null) {
            return null;
        }
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String tokenKey = "user:token:" + token;
        try {
            Object id = redisTemplate.opsForValue().get(tokenKey);
            if (id != null) {
                return Long.parseLong(id.toString());
            }
        } catch (Exception e) {
            // Redis failed, try database fallback
        }

        User user = userService.getUserByUsernameFromSession(token);
        if (user != null) {
            return user.getId();
        }

        return null;
    }
}