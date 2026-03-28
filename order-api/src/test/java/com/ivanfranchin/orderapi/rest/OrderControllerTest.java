package com.ivanfranchin.orderapi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanfranchin.orderapi.order.Order;
import com.ivanfranchin.orderapi.order.OrderNotFoundException;
import com.ivanfranchin.orderapi.order.OrderService;
import com.ivanfranchin.orderapi.rest.dto.CreateOrderRequest;
import com.ivanfranchin.orderapi.security.CustomUserDetails;
import com.ivanfranchin.orderapi.security.Role;
import com.ivanfranchin.orderapi.security.SecurityConfig;
import com.ivanfranchin.orderapi.security.TokenProvider;
import com.ivanfranchin.orderapi.user.User;
import com.ivanfranchin.orderapi.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenProvider tokenProvider;

    private User buildUser(String username, Role role) {
        User user = new User(username, "pass", "Test User", username + "@example.com", role);
        user.setId(1L);
        return user;
    }

    private Order buildOrder(String id, String description, User user) {
        Order order = new Order(description);
        order.setId(id);
        order.setUser(user);
        order.setCreatedAt(Instant.now());
        return order;
    }

    private CustomUserDetails buildCustomUserDetails(String username, Role role) {
        return new CustomUserDetails(
                1L,
                username,
                "pass",
                "Test User",
                username + "@example.com",
                List.of(new SimpleGrantedAuthority(role.name()))
        );
    }

    // -- GET /api/orders --

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void getOrders_returns200AsAdmin() throws Exception {
        User user = buildUser("admin", Role.ADMIN);
        Order order = buildOrder("id-1", "Buy iPhone", user);
        when(orderService.getOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Buy iPhone"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void getOrders_withTextParam_returns200AsAdmin() throws Exception {
        User user = buildUser("admin", Role.ADMIN);
        Order order = buildOrder("id-1", "Buy iPhone", user);
        when(orderService.getOrdersContainingText("iphone")).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders").param("text", "iphone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void getOrders_returns403AsUser() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrders_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    // -- POST /api/orders --

    @Test
    void createOrder_returns201AsAdmin() throws Exception {
        User user = buildUser("admin", Role.ADMIN);
        CustomUserDetails adminDetails = buildCustomUserDetails("admin", Role.ADMIN);
        CreateOrderRequest request = new CreateOrderRequest("Buy two iPhones");
        Order savedOrder = buildOrder(UUID.randomUUID().toString(), "Buy two iPhones", user);

        when(userService.validateAndGetUserByUsername("admin")).thenReturn(user);
        when(orderService.saveOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/api/orders")
                        .with(user(adminDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Buy two iPhones"));
    }

    @Test
    void createOrder_returns201AsUser() throws Exception {
        User user = buildUser("user", Role.USER);
        CustomUserDetails userDetails = buildCustomUserDetails("user", Role.USER);
        CreateOrderRequest request = new CreateOrderRequest("Buy MacBook");
        Order savedOrder = buildOrder(UUID.randomUUID().toString(), "Buy MacBook", user);

        when(userService.validateAndGetUserByUsername("user")).thenReturn(user);
        when(orderService.saveOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/api/orders")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Buy MacBook"));
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void createOrder_returns400WhenDescriptionBlank() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_returns401WhenUnauthenticated() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("Buy iPhone");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // -- DELETE /api/orders/{id} --

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void deleteOrder_returns204WhenFoundAsAdmin() throws Exception {
        User user = buildUser("admin", Role.ADMIN);
        String id = UUID.randomUUID().toString();
        Order order = buildOrder(id, "Buy iPhone", user);
        when(orderService.validateAndGetOrder(id)).thenReturn(order);

        mockMvc.perform(delete("/api/orders/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void deleteOrder_returns404WhenNotFound() throws Exception {
        String id = UUID.randomUUID().toString();
        when(orderService.validateAndGetOrder(id))
                .thenThrow(new OrderNotFoundException("Order with id " + id + " not found"));

        mockMvc.perform(delete("/api/orders/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void deleteOrder_returns403AsUser() throws Exception {
        mockMvc.perform(delete("/api/orders/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteOrder_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/orders/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}
