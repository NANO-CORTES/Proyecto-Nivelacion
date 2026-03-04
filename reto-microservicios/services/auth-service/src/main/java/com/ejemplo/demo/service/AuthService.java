package com.ejemplo.demo.service;

import com.ejemplo.demo.dto.AuthRequest;
import com.ejemplo.demo.entity.User;
import com.ejemplo.demo.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    public String login(AuthRequest authRequest) {


        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setRole("ROLE_USER");

        return jwtProvider.createToken(user.getUsername(), user.getRole());
    }
}