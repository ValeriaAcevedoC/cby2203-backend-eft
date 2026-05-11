package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationRequestTest {

    @Test
    void testSettersAndGetters() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("user");
        request.setEmail("user@example.com");
        request.setPassword("pass");
        assertEquals("user", request.getUsername());
        assertEquals("user@example.com", request.getEmail());
        assertEquals("pass", request.getPassword());
    }

}