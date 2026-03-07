package com.ejemplo.demo.service;

import com.ejemplo.demo.dto.AuthRequest;
import com.ejemplo.demo.entity.User;
import com.ejemplo.demo.repository.UserRepository;
import com.ejemplo.demo.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(JwtProvider jwtProvider, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        } else if (user.getRole().equalsIgnoreCase("ROLE_ADMIN") || user.getRole().equalsIgnoreCase("ADMIN")) {
            user.setRole("ROLE_ADMIN");
        } else {
            // Cualquier otro valor (USER, ROLE_USER, user, etc.) → ROLE_USER
            user.setRole("ROLE_USER");
        }

        return userRepository.save(user);
    }

    public String login(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return jwtProvider.createToken(user.getUsername(), user.getRole());
    }
}