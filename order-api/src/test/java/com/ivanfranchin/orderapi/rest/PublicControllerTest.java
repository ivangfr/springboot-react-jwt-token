package com.ivanfranchin.orderapi.rest;

import com.ivanfranchin.orderapi.order.OrderService;
import com.ivanfranchin.orderapi.security.SecurityConfig;
import com.ivanfranchin.orderapi.security.TokenProvider;
import com.ivanfranchin.orderapi.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicController.class)
@Import(SecurityConfig.class)
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenProvider tokenProvider;

    // -- GET /public/numberOfUsers --

    @Test
    void getNumberOfUsers_returns200WithoutAuth() throws Exception {
        when(userService.countUsers()).thenReturn(2L);

        mockMvc.perform(get("/public/numberOfUsers"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void getNumberOfUsers_returnsZeroWhenNoUsers() throws Exception {
        when(userService.countUsers()).thenReturn(0L);

        mockMvc.perform(get("/public/numberOfUsers"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    // -- GET /public/numberOfOrders --

    @Test
    void getNumberOfOrders_returns200WithoutAuth() throws Exception {
        when(orderService.countOrders()).thenReturn(3L);

        mockMvc.perform(get("/public/numberOfOrders"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void getNumberOfOrders_returnsZeroWhenNoOrders() throws Exception {
        when(orderService.countOrders()).thenReturn(0L);

        mockMvc.perform(get("/public/numberOfOrders"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
