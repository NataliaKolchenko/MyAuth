package com.example.demo.controller;

import com.example.demo.service.jwt.JwtSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenValidationController {

    private final JwtSecurityService jwtSecurityService;

    @PostMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        boolean isValid = jwtSecurityService.validateTokenFromHeader(authHeader);
        return ResponseEntity.ok(isValid);
    }
}

