package com.ivanfranchin.orderapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenProviderTest {

    // HS512 requires at least 512 bits (64 chars)
    private static final String SECRET = "v9y$B&E)H@MbQeThWmZq4t7w!z%C*F-JaNdRfUjXn2r5u8x/A?D(G+KbPeShVkYp";

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMinutes", 10L);
    }

    // -- generate --

    @Test
    void generate_returnsNonBlankToken() {
        String token = tokenProvider.generate(mockAuthentication("alice", "ADMIN"));

        assertThat(token).isNotBlank();
    }

    @Test
    void generate_tokenRoundTripsSubjectAndRole() {
        String token = tokenProvider.generate(mockAuthentication("alice", "ADMIN"));

        Optional<Jws<Claims>> jws = tokenProvider.validateTokenAndGetJws(token);

        assertThat(jws).isPresent();
        assertThat(jws.get().getPayload().getSubject()).isEqualTo("alice");
        assertThat(jws.get().getPayload().get("rol", List.class)).containsExactly("ADMIN");
    }

    @Test
    void generate_tokenContainsCorrectIssuerAndAudience() {
        String token = tokenProvider.generate(mockAuthentication("alice", "USER"));

        Optional<Jws<Claims>> jws = tokenProvider.validateTokenAndGetJws(token);

        assertThat(jws).isPresent();
        assertThat(jws.get().getPayload().getIssuer()).isEqualTo(TokenProvider.TOKEN_ISSUER);
        assertThat(jws.get().getPayload().getAudience()).containsExactly(TokenProvider.TOKEN_AUDIENCE);
    }

    // -- validateTokenAndGetJws --

    @Test
    void validateTokenAndGetJws_returnsEmptyForMalformedToken() {
        Optional<Jws<Claims>> result = tokenProvider.validateTokenAndGetJws("not.a.token");

        assertThat(result).isEmpty();
    }

    @Test
    void validateTokenAndGetJws_returnsEmptyForTokenSignedWithWrongKey() {
        TokenProvider other = new TokenProvider();
        ReflectionTestUtils.setField(other, "jwtSecret", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        ReflectionTestUtils.setField(other, "jwtExpirationMinutes", 10L);

        String tokenFromOtherKey = other.generate(mockAuthentication("alice", "USER"));

        Optional<Jws<Claims>> result = tokenProvider.validateTokenAndGetJws(tokenFromOtherKey);

        assertThat(result).isEmpty();
    }

    @Test
    void validateTokenAndGetJws_returnsEmptyForExpiredToken() {
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMinutes", 0L);
        String expiredToken = tokenProvider.generate(mockAuthentication("alice", "USER"));

        Optional<Jws<Claims>> result = tokenProvider.validateTokenAndGetJws(expiredToken);

        assertThat(result).isEmpty();
    }

    @Test
    void validateTokenAndGetJws_returnsEmptyForNullToken() {
        Optional<Jws<Claims>> result = tokenProvider.validateTokenAndGetJws(null);

        assertThat(result).isEmpty();
    }

    // -- helpers --

    private Authentication mockAuthentication(String username, String role) {
        CustomUserDetails userDetails = new CustomUserDetails(
                1L, username, "pass", "Test User", username + "@example.com",
                List.of(new SimpleGrantedAuthority(role))
        );
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        return authentication;
    }
}
