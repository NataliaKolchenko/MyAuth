package com.example.demo.service.jwt;

import com.example.demo.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtSecurityService {
    private static final String SECRET_KEY = "5Hdo5+PxMJkLQ9Wo7WnYMR/gBzTfC5XrB3iNPvMlscY=";

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


//    public String generateToken(UserDetails userDetails){
//        return Jwts.builder()
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
//                .signWith(getSigningKey())
//                .compact();
//    }

    //  собирает claims
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof AppUser customUserDetails) {
//            claims.put("id", customUserDetails.getId());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails.getUsername());
    }

    //генерирует токен
    private String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)  // Устанавливаем все claims
                .setSubject(subject) // Устанавливаем subject (username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


//    public String generateToken(UserDetails userDetails){
//        String role = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority) // Преобразуем список ролей в строку
//                .findFirst()
//                .orElse("USER"); // Укажите роль по умолчанию, если ее нет
//
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .claim("role", role) // Добавляем роль в токен
//                .claim("userId", )
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // срок действия - 24 минуты
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }

    public String generateRefreshToken(Map<String, String> claims, UserDetails userDetails){
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim (String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Получение имени юзера (он же почта)
    public String extractUsername (String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Когда срок действия заканчивается
    public Date extractExpiration (String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Когда выдан токен
    public Date extractIssuedAt (String token){
        return extractClaim(token, Claims::getIssuedAt);
    }

    // Метод проверки срока действия токена
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Метод для валидации токена
    public boolean validateToken (String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }
}
