package com.unilex.backend.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
@ConfigurationProperties(prefix = "jwt")   // ← 自动把 7d 解析成 Duration
@Setter
public class JwtUtil {

    private String secret;
    private Duration expiration;            // Spring 已帮你转好

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        long expMillis = System.currentTimeMillis() + expiration.toMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expMillis))
                .signWith(getKey())
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
// 功能模块：JWT 工具