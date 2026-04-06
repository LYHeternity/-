package com.entrepreneur.platform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：生成与解析令牌
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expire-hours}")
    private double expireHours;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT，包含 userId、username、role
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + (long)(expireHours * 3600L * 1000));
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims c = parseToken(token);
        return c != null ? Long.parseLong(c.getSubject()) : null;
    }

    public String getUsernameFromToken(String token) {
        Claims c = parseToken(token);
        return c != null ? (String) c.get("username") : null;
    }

    public String getRoleFromToken(String token) {
        Claims c = parseToken(token);
        return c != null ? (String) c.get("role") : null;
    }

    public boolean validateToken(String token) {
        try {
            Claims c = parseToken(token);
            return c != null && !c.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
