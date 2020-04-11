package com.mycompany.orderapi.rest;

import com.mycompany.orderapi.rest.dto.JwtRequest;
import com.mycompany.orderapi.rest.dto.JwtResponse;
import com.mycompany.orderapi.security.CustomUserDetails;
import com.mycompany.orderapi.security.TokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AuthController(UserDetailsService userDetailsService, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/token")
    public JwtResponse createJwtToken(@Valid @RequestBody JwtRequest jwtRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        final String jwtToken = tokenProvider.generate(authentication);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        return new JwtResponse(customUserDetails.getId(), customUserDetails.getUsername(), customUserDetails.getAuthorities(), jwtToken);
    }

}
