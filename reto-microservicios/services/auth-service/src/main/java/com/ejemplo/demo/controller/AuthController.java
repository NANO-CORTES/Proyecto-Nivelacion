package com.ejemplo.demo.controller;

import com.ejemplo.demo.entity.User;
import com.ejemplo.demo.dto.AuthRequest;
import com.ejemplo.demo.dto.TokenDTO;
import com.ejemplo.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody AuthRequest authRequest) {
        String token = authService.login(authRequest);
        return ResponseEntity.ok(new TokenDTO(token));
    }
}