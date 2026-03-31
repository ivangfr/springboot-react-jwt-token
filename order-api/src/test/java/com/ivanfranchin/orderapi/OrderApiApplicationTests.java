package com.ivanfranchin.orderapi;

import com.ivanfranchin.orderapi.order.OrderService;
import com.ivanfranchin.orderapi.security.TokenProvider;
import com.ivanfranchin.orderapi.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.sql.DataSource;

@SpringBootTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
        "spring.jpa.hibernate.ddl-auto=none"
})
class OrderApiApplicationTests {

    @MockitoBean
    DataSource dataSource;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void contextLoads() {
    }
}
