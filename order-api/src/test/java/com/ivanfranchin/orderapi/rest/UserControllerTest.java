package com.ivanfranchin.orderapi.rest;

import com.ivanfranchin.orderapi.security.CustomUserDetails;
import com.ivanfranchin.orderapi.security.SecurityConfig;
import com.ivanfranchin.orderapi.security.TokenProvider;
import com.ivanfranchin.orderapi.user.User;
import com.ivanfranchin.orderapi.user.UserDeletionNotAllowedException;
import com.ivanfranchin.orderapi.user.UserNotFoundException;
import com.ivanfranchin.orderapi.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenProvider tokenProvider;

    private User buildUser(String username, String role) {
        User user = new User(username, "pass", "Test User", username + "@example.com", role);
        user.setId(1L);
        return user;
    }

    private CustomUserDetails buildCustomUserDetails(String username, String role) {
        return new CustomUserDetails(
                1L,
                username,
                "pass",
                "Test User",
                username + "@example.com",
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    // -- GET /api/users/me --

    @Test
    void getMe_returns200AsUser() throws Exception {
        CustomUserDetails userDetails = buildCustomUserDetails("user", "USER");
        User user = buildUser("user", "USER");
        when(userService.validateAndGetUserByUsername("user")).thenReturn(user);

        mockMvc.perform(get("/api/users/me").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void getMe_returns200AsAdmin() throws Exception {
        CustomUserDetails adminDetails = buildCustomUserDetails("admin", "ADMIN");
        User admin = buildUser("admin", "ADMIN");
        when(userService.validateAndGetUserByUsername("admin")).thenReturn(admin);

        mockMvc.perform(get("/api/users/me").with(user(adminDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void getMe_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    // -- GET /api/users --

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void getUsers_returns200AsAdmin() throws Exception {
        User user1 = buildUser("alice", "USER");
        User user2 = buildUser("bob", "USER");
        when(userService.getUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void getUsers_returns403AsUser() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUsers_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    // -- GET /api/users/{username} --

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void getUser_returns200WhenFoundAsAdmin() throws Exception {
        User user = buildUser("alice", "USER");
        when(userService.validateAndGetUserByUsername("alice")).thenReturn(user);

        mockMvc.perform(get("/api/users/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void getUser_returns404WhenNotFound() throws Exception {
        when(userService.validateAndGetUserByUsername("ghost"))
                .thenThrow(new UserNotFoundException("User with username ghost not found"));

        mockMvc.perform(get("/api/users/ghost"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void getUser_returns403AsUser() throws Exception {
        mockMvc.perform(get("/api/users/alice"))
                .andExpect(status().isForbidden());
    }

    // -- DELETE /api/users/{username} --

    @Test
    void deleteUser_returns204WhenFoundAsAdmin() throws Exception {
        CustomUserDetails adminDetails = buildCustomUserDetails("admin", "ADMIN");
        User alice = buildUser("alice", "USER");
        when(userService.validateAndGetUserByUsername("alice")).thenReturn(alice);

        mockMvc.perform(delete("/api/users/alice").with(user(adminDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_returns400WhenAdminDeletesOwnAccount() throws Exception {
        CustomUserDetails adminDetails = buildCustomUserDetails("admin", "ADMIN");
        User admin = buildUser("admin", "ADMIN");
        when(userService.validateAndGetUserByUsername("admin")).thenReturn(admin);

        mockMvc.perform(delete("/api/users/admin").with(user(adminDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_returns400WhenDeletingLastAdmin() throws Exception {
        CustomUserDetails adminDetails = buildCustomUserDetails("admin", "ADMIN");
        User otherAdmin = buildUser("other-admin", "ADMIN");
        when(userService.validateAndGetUserByUsername("other-admin")).thenReturn(otherAdmin);
        when(userService.countAdmins()).thenReturn(1L);

        mockMvc.perform(delete("/api/users/other-admin").with(user(adminDetails)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void deleteUser_returns404WhenNotFound() throws Exception {
        when(userService.validateAndGetUserByUsername("ghost"))
                .thenThrow(new UserNotFoundException("User with username ghost not found"));

        mockMvc.perform(delete("/api/users/ghost"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void deleteUser_returns403AsUser() throws Exception {
        mockMvc.perform(delete("/api/users/alice"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/users/alice"))
                .andExpect(status().isUnauthorized());
    }
}
