package com.example.shortlink.service;

import com.example.shortlink.dto.LoginRequest;
import com.example.shortlink.dto.LoginResponse;
import com.example.shortlink.entity.User;

import java.util.List;

public interface UserService {
    LoginResponse login(LoginRequest request);

    void logout(String token);

    User getUserById(Long userId);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    void updateUserStatus(Long userId, Integer status);

    User createUser(String username, String password, String email);

    User getUserByUsernameFromSession(String sessionToken);

    boolean validateToken(String token);
}