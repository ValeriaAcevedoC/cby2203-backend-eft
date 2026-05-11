package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecuredControllerTest {

    @Test
    void testGreetings() {
        SecuredController controller = new SecuredController();
        String result = controller.greetings("Test");
        assertEquals("Hello {Test}", result);
    }

    @Test
    void testGreetingsDefault() {
        SecuredController controller = new SecuredController();
        String result = controller.greetings("World");
        assertEquals("Hello {World}", result);
    }

}