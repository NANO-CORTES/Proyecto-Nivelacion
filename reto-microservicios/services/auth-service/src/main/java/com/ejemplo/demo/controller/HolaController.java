package com.ejemplo.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // Esto es vital para que el Gateway te encuentre
public class HolaController {

    @GetMapping("/hola") // Esta es la ruta final
    public String saludar() {
        return "Hola, el microservicio de autenticación responde";
    }
}