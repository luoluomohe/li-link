package com.example.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.dto.LoginRequest;
import com.example.shortlink.dto.LoginResponse;
import com.example.shortlink.entity.User;
import com.example.shortlink.mapper.UserMapper;
import com.example.shortlink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "user:token:";

    @Override
    public LoginResponse login(LoginRequest request) {
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username: " + request.getUsername());
        System.out.println("Password: " + request.getPassword());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            System.out.println("User not found");
            return null;
        }

        System.out.println("User found: " + user.getUsername());
        System.out.println("User status: " + user.getStatus());
        System.out.println("Stored password: " + user.getPassword());

        String passwordMd5 = DigestUtils.md5DigestAsHex((request.getPassword()).getBytes(StandardCharsets.UTF_8));
        System.out.println("Input password MD5: " + passwordMd5);

        if (!passwordMd5.equals(user.getPassword())) {
            System.out.println("Password mismatch");
            return null;
        }

        if (user.getStatus() != 1) {
            System.out.println("User status not active");
            return null;
        }
        System.out.println("Login successful");

        String token = UUID.randomUUID().toString().replace("-", "");

        String tokenKey = TOKEN_KEY_PREFIX + token;
        try {
            redisTemplate.opsForValue().set(tokenKey, user.getId(), 7, TimeUnit.DAYS);
        } catch (Exception e) {
            System.out.println("Redis set failed, using database fallback: " + e.getMessage());
        }

        user.setSessionToken(token);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setIsAdmin(user.getIsAdmin());
        return response;
    }

    @Override
    public void logout(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        redisTemplate.delete(tokenKey);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(status);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public User createUser(String username, String password, String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User existing = userMapper.selectOne(wrapper);
        if (existing != null) {
            return existing;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        user.setEmail(email);
        user.setStatus(1);
        user.setIsAdmin(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserByUsernameFromSession(String sessionToken) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getSessionToken, sessionToken);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean validateToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
        } catch (Exception e) {
            System.out.println("Redis validate failed, using database fallback: " + e.getMessage());
            return getUserByUsernameFromSession(token) != null;
        }
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        String oldPasswordMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes(StandardCharsets.UTF_8));
        if (!oldPasswordMd5.equals(user.getPassword())) {
            return false;
        }

        String newPasswordMd5 = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        user.setPassword(newPasswordMd5);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return true;
    }

    @Override
    public boolean changeUsername(Long userId, String newUsername) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, newUsername);
        wrapper.ne(User::getId, userId);
        User existing = userMapper.selectOne(wrapper);
        if (existing != null) {
            return false;
        }

        user.setUsername(newUsername);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return true;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        String newPasswordMd5 = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        user.setPassword(newPasswordMd5);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return true;
    }
}