package com.example.demo.model;

import com.example.demo.enums.AppRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="app_user")
public class AppUser implements UserDetails {
    @Id
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.ORDINAL)
    private AppRole role;

    //This method returns a collection of user rights (roles). In your case, it returns a list with one element - the user role converted to SimpleGrantedAuthority. This is used by Spring Security to determine which resources the user has access to.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    //Returns a unique user ID. In your implementation, email is used as the username. Spring Security uses this value to identify the user in the system.
    @Override
    public String getUsername() {
        return email;
    }

    //Indicates whether the user's account has expired. By returning true, you indicate that the account will never expire. If you wanted to implement account expiration functionality, you could change this logic.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //Determines whether the user's account is not blocked. By returning true, you indicate that the account is never blocked. This can be used to implement temporary blocking of users, for example, after several unsuccessful login attempts.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //Indicates whether the user's credentials (usually password) have expired. By returning true, you indicate that the credentials will never expire. This can be useful if you want to implement a policy of regularly changing passwords.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //Determines whether the user's account is active. By returning true, you indicate that the account is always active. This method can be used to implement account deactivation functionality, for example, when deleting a user or if email confirmation is required.
    @Override
    public boolean isEnabled() {
        return true;
    }
}