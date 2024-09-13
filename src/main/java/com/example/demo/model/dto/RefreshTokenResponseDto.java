package com.example.demo.model.dto;

import lombok.Data;
//Ответ на “сброс” токена
@Data
public class RefreshTokenResponseDto {
    private String jwtToken;
    private String refreshToken;
}
