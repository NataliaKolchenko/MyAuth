package com.example.demo.model.dto;

import lombok.Data;
//“Сброс” токена
@Data
public class RefreshTokenRequestDto {
    private String refreshToken;
}
