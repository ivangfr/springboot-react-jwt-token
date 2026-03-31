package com.ivanfranchin.orderapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticationFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenProvider tokenProvider;

    private TokenAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new TokenAuthenticationFilter(userDetailsService, tokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validBearerToken_populatesSecurityContext() throws Exception {
        String token = "valid.jwt.token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        @SuppressWarnings("unchecked")
        Jws<Claims> jws = mock(Jws.class);
        Claims claims = mock(Claims.class);
        when(jws.getPayload()).thenReturn(claims);
        when(claims.getSubject()).thenReturn("alice");

        CustomUserDetails userDetails = new CustomUserDetails(
                1L, "alice", "pass", "Alice", "alice@example.com",
                List.of(new SimpleGrantedAuthority("USER"))
        );

        when(request.getHeader(TokenAuthenticationFilter.TOKEN_HEADER))
                .thenReturn(TokenAuthenticationFilter.TOKEN_PREFIX + token);
        when(tokenProvider.validateTokenAndGetJws(token)).thenReturn(Optional.of(jws));
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("alice");
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_expiredToken_leavesSecurityContextEmpty() throws Exception {
        String token = "expired.jwt.token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(TokenAuthenticationFilter.TOKEN_HEADER))
                .thenReturn(TokenAuthenticationFilter.TOKEN_PREFIX + token);
        when(tokenProvider.validateTokenAndGetJws(token)).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_leavesSecurityContextEmpty() throws Exception {
        String token = "invalid.jwt.token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(TokenAuthenticationFilter.TOKEN_HEADER))
                .thenReturn(TokenAuthenticationFilter.TOKEN_PREFIX + token);
        when(tokenProvider.validateTokenAndGetJws(token)).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_missingAuthorizationHeader_leavesSecurityContextEmpty() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(TokenAuthenticationFilter.TOKEN_HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_malformedPrefix_leavesSecurityContextEmpty() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        // "Token " instead of "Bearer "
        when(request.getHeader(TokenAuthenticationFilter.TOKEN_HEADER))
                .thenReturn("Token some.jwt.value");

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_exceptionFromUserDetailsService_stillCallsFilterChain() throws Exception {
        String token = "valid.jwt.token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        @SuppressWarnings("unchecked")
        Jws<Claims> jws = mock(Jws.class);
        Claims claims = mock(Claims.class);
        when(jws.getPayload()).thenReturn(claims);
        when(claims.getSubject()).thenReturn("alice");

        when(request.getHeader(TokenAuthenticationFilter.TOKEN_HEADER))
                .thenReturn(TokenAuthenticationFilter.TOKEN_PREFIX + token);
        when(tokenProvider.validateTokenAndGetJws(token)).thenReturn(Optional.of(jws));
        when(userDetailsService.loadUserByUsername("alice"))
                .thenThrow(new RuntimeException("DB error"));

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }
}
