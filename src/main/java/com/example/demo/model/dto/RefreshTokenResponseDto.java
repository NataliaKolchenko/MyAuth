package com.example.demo.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//Ответ на “сброс” токена
@Getter
@Setter
public class RefreshTokenResponseDto {
    private String jwtToken;
    private String refreshToken;
}
