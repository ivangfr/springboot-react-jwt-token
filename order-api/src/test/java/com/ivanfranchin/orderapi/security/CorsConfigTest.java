package com.ivanfranchin.orderapi.security;

import com.ivanfranchin.orderapi.order.OrderService;
import com.ivanfranchin.orderapi.rest.PublicController;
import com.ivanfranchin.orderapi.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(PublicController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class CorsConfigTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenProvider tokenProvider;

    private MockMvc mockMvc() {
        return webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void preflight_allowedOrigin_receivesCorsHeaders() throws Exception {
        mockMvc().perform(options("/public/numberOfUsers")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", HttpMethod.GET.name()))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void preflight_disallowedOrigin_doesNotReceiveCorsHeaders() throws Exception {
        mockMvc().perform(options("/public/numberOfUsers")
                        .header("Origin", "http://evil.example.com")
                        .header("Access-Control-Request-Method", HttpMethod.GET.name()))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void simpleGet_allowedOrigin_receivesAllowOriginHeader() throws Exception {
        mockMvc().perform(get("/public/numberOfUsers")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void simpleGet_disallowedOrigin_doesNotReceiveAllowOriginHeader() throws Exception {
        mockMvc().perform(get("/public/numberOfUsers")
                        .header("Origin", "http://evil.example.com"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void preflight_wildcardMethodsAndHeaders_areReflectedInResponse() throws Exception {
        mockMvc().perform(options("/public/numberOfUsers")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", HttpMethod.POST.name())
                        .header("Access-Control-Request-Headers", "X-Custom-Header, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }

}
