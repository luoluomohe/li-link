package com.example.shortlink.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.entity.User;
import com.example.shortlink.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Long userId = null;

                try {
                    String tokenKey = "user:token:" + token;
                    Object id = redisTemplate.opsForValue().get(tokenKey);
                    if (id != null) {
                        userId = Long.parseLong(id.toString());
                    }
                } catch (Exception e) {
                    System.out.println("Redis get failed, using database fallback: " + e.getMessage());
                }

                User user = null;
                if (userId == null) {
                    user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getSessionToken, token));
                    if (user != null) {
                        userId = user.getId();
                    }
                } else {
                    user = userMapper.selectById(userId);
                }

                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.out.println("Token validation error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
