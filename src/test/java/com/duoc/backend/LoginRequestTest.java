package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testConstructorAndGetters() {
        LoginRequest request = new LoginRequest("user", "pass");
        assertEquals("user", request.getUsername());
        assertEquals("pass", request.getPassword());
    }

    @Test
    void testSetters() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");
        assertEquals("user", request.getUsername());
        assertEquals("pass", request.getPassword());
    }

}