package com.reto.api_gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class JwtUtils {

    // ESTA CLAVE DEBE SER IDÉNTICA A LA DEL AUTH-SERVICE
    private final String SECRET_KEY = "EstaEsUnaClaveSuperSecretaDeMasDe32Caracteres12345";

    public void validateToken(String token) {
        // Convertimos la frase secreta en una llave real para la librería
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        // Validamos que el token sea original y no haya expirado
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
    }
}