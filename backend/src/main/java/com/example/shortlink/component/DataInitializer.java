package com.example.shortlink.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.entity.User;
import com.example.shortlink.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) {
        System.out.println("DataInitializer is running...");
        try {
            User existingAdmin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
            String password = "admin123";
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
            
            if (existingAdmin == null) {
                System.out.println("Creating default admin user...");
                User admin = new User();
                admin.setUsername("admin");
                System.out.println("Password: " + password + ", MD5: " + md5Password);
                admin.setPassword(md5Password);
                admin.setEmail("admin@example.com");
                admin.setStatus(1);
                admin.setIsAdmin(1);
                admin.setCreateTime(LocalDateTime.now());
                admin.setUpdateTime(LocalDateTime.now());
                int result = userMapper.insert(admin);
                System.out.println("Insert result: " + result);
                System.out.println("Default admin user created: admin / admin123");
            } else {
                System.out.println("Admin user already exists: " + existingAdmin.getUsername());
                System.out.println("Current password: " + existingAdmin.getPassword());
                System.out.println("Current status: " + existingAdmin.getStatus());
                
                // 强制更新密码和状态
                existingAdmin.setPassword(md5Password);
                existingAdmin.setStatus(1);
                existingAdmin.setUpdateTime(LocalDateTime.now());
                int result = userMapper.updateById(existingAdmin);
                System.out.println("Update result: " + result);
                System.out.println("Updated admin user: admin / admin123");
                System.out.println("New password: " + md5Password);
                System.out.println("New status: 1");
            }
        } catch (Exception e) {
            System.out.println("Error creating admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}