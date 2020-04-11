package com.mycompany.orderapi.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
public class JwtResponse {

    private Long id;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private String jwtToken;

}
