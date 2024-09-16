package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//Логин пользователя
@Getter
@Setter
public class LoginRequestDto {
    private String email;
    private String password;
}
