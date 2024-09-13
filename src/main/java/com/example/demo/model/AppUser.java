package com.example.demo.model;

import com.example.demo.enums.AppRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name="app_user")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fullName;
    private String email;
    private String password;
    private AppRole role;

    //Этот метод возвращает коллекцию прав (ролей) пользователя. В вашем случае он возвращает список с одним элементом - ролью пользователя, преобразованной в SimpleGrantedAuthority. Это используется Spring Security для определения, к каким ресурсам пользователь имеет доступ.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    //Возвращает уникальный идентификатор пользователя. В вашей реализации в качестве имени пользователя используется email. Spring Security использует это значение для идентификации пользователя в системе.
    @Override
    public String getUsername() {
        return email;
    }

    //Показывает, не истек ли срок действия аккаунта пользователя. Возвращая true, вы указываете, что срок действия аккаунта никогда не истекает. Если бы вы хотели реализовать функционал истечения срока действия аккаунтов, вы могли бы изменить эту логику.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //Определяет, не заблокирован ли аккаунт пользователя. Возвращая true, вы указываете, что аккаунт никогда не блокируется. Это можно использовать для реализации временной блокировки пользователей, например, после нескольких неудачных попыток входа.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //Показывает, не истек ли срок действия учетных данных (обычно пароля) пользователя. Возвращая true, вы указываете, что срок действия учетных данных никогда не истекает. Это может быть полезно, если вы хотите реализовать политику регулярной смены паролей.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //Определяет, активен ли аккаунт пользователя. Возвращая true, вы указываете, что аккаунт всегда активен. Этот метод можно использовать для реализации функционала деактивации аккаунтов, например, при удалении пользователя или при необходимости подтверждения email.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
