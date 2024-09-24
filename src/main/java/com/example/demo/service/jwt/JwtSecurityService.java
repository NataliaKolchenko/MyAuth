package com.example.demo.service.jwt;

import com.example.demo.repository.AppUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtSecurityService {
    private static final String SECRET_KEY = "5Hdo5+PxMJkLQ9Wo7WnYMR/gBzTfC5XrB3iNPvMlscY=";
    private final AppUserService appUserService;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", appUserService.getUserInfo(userDetails).getRole())
                .claim("userId", appUserService.getUserInfo(userDetails).getId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Map<String, String> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Получение имени юзера (он же почта)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Когда срок действия заканчивается
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Когда выдан токен
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    // Метод проверки срока действия токена
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Метод для валидации токена
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    //проверяет валидность токена, полученного из заголовка запроса.
    public boolean validateTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);

        try {
            String username = extractUsername(token);
            UserDetails userDetails = appUserService
                    .getDetailsService()
                    .loadUserByUsername(username);
            return validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}
