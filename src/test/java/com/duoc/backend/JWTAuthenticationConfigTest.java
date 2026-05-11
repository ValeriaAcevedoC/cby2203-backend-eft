package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JWTAuthenticationConfigTest {

    @Test
    void testGetJWTToken() {
        JWTAuthenticationConfig config = new JWTAuthenticationConfig();
        String token = config.getJWTToken("user");
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
    }

}