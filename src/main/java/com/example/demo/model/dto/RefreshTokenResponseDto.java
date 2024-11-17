package com.example.demo.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenResponseDto {
    private String jwtToken;
    private String refreshToken;
}
