package com.ivanfranchin.orderapi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanfranchin.orderapi.rest.dto.LoginRequest;
import com.ivanfranchin.orderapi.rest.dto.SignUpRequest;
import com.ivanfranchin.orderapi.security.SecurityConfig;
import com.ivanfranchin.orderapi.security.TokenProvider;
import com.ivanfranchin.orderapi.user.User;
import com.ivanfranchin.orderapi.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // -- POST /auth/authenticate --

    @Test
    void authenticate_returnsTokenOnValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("user", "password");
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password", List.of());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.generate(any(Authentication.class))).thenReturn("test-jwt-token");

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-jwt-token"));
    }

    @Test
    void authenticate_returns400WhenUsernameBlank() throws Exception {
        LoginRequest request = new LoginRequest("", "password");

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticate_returns400WhenPasswordBlank() throws Exception {
        LoginRequest request = new LoginRequest("user", "");

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -- POST /auth/signup --

    @Test
    void signUp_returns201WithTokenOnSuccess() throws Exception {
        SignUpRequest request = new SignUpRequest("newuser", "password", "New User", "new@example.com");
        when(userService.hasUserWithUsername("newuser")).thenReturn(false);
        when(userService.hasUserWithEmail("new@example.com")).thenReturn(false);
        when(userService.saveUser(any(User.class))).thenReturn(new User("newuser", "encoded", "New User", "new@example.com", "USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken("newuser", "password", List.of());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.generate(any(Authentication.class))).thenReturn("new-jwt-token");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("new-jwt-token"));
    }

    @Test
    void signUp_returns409WhenUsernameAlreadyUsed() throws Exception {
        SignUpRequest request = new SignUpRequest("existinguser", "password", "Existing", "other@example.com");
        when(userService.hasUserWithUsername("existinguser")).thenReturn(true);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void signUp_returns409WhenEmailAlreadyUsed() throws Exception {
        SignUpRequest request = new SignUpRequest("newuser", "password", "New User", "existing@example.com");
        when(userService.hasUserWithUsername("newuser")).thenReturn(false);
        when(userService.hasUserWithEmail("existing@example.com")).thenReturn(true);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void signUp_returns400WhenUsernameBlank() throws Exception {
        SignUpRequest request = new SignUpRequest("", "password", "Name", "email@example.com");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_returns400WhenEmailInvalid() throws Exception {
        SignUpRequest request = new SignUpRequest("newuser", "password", "Name", "not-an-email");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
