package com.epam.rd.autocode.assessment.appliances.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final int JWT_EXPIRATION_IN_MINUTES;
    private final String SECRET_KEY = System.getenv("SECRET_SIGN_JWT_KEY");
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    public JwtUtil(@Value("${jwt.expiration.minutes}") int expirationInMinutes) {
        JWT_EXPIRATION_IN_MINUTES = expirationInMinutes;
    }

    public String generateToken(String userId, String role) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_IN_MINUTES * 1000 * 60L))
                .signWith(key)
                .compact();
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return "ROLE_" + extractAllClaims(token).get("role").toString();
    }

    public boolean isTokenValid(String token) {
        if (token == null) return false;
        return extractExpiration(token).after(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
