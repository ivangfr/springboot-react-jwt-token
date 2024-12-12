package com.ivanfranchin.orderapi.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
}
