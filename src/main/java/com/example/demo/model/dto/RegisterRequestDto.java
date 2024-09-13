package com.example.demo.model.dto;

import lombok.Data;

//Регистрация пользователя
@Data
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
}
