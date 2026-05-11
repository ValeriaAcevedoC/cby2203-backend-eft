package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void testConstructorAndGetters() {
        UserResponse response = new UserResponse(1, "user", "user@example.com");
        assertEquals(1, response.getId());
        assertEquals("user", response.getUsername());
        assertEquals("user@example.com", response.getEmail());
    }

}