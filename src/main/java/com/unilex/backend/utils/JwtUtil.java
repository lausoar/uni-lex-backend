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
@ConfigurationProperties(prefix = "jwt")
@Setter
public class JwtUtil {

    private String secret;
    private Duration expiration;

    private static final String TOKEN_VERSION_CLAIM = "token_version";

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成token，携带token版本号
     */
    public String generateToken(String username, Integer tokenVersion) {
        long expMillis = System.currentTimeMillis() + expiration.toMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expMillis))
                .claim(TOKEN_VERSION_CLAIM, tokenVersion) // 添加版本号
                .signWith(getKey())
                .compact();
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 获取token中的版本号
     */
    public Integer getTokenVersion(String token) {
        return parseClaims(token).get(TOKEN_VERSION_CLAIM, Integer.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}