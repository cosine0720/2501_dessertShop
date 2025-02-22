package com.dessert.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
//    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
//            "ThisIsA32CharacterLongSecretKey!".getBytes()
//    );

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // 轉換 UUID 為 Base64 格式
        String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = base64Secret.getBytes(StandardCharsets.UTF_8);

        // 確保密鑰至少 32 字節（對於 HS256）
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes");
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

//    public JwtUtil() {
//        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
//        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
//    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 設置過期時間
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {

        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
