package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testRegisterUser() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("user");
        request.setEmail("user@example.com");
        request.setPassword("pass");
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setEmail("user@example.com");
        when(userService.registerUser(request)).thenReturn(user);

        ResponseEntity<?> response = userController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserResponse expected = new UserResponse(1, "user", "user@example.com");
        assertEquals(expected.getId(), ((UserResponse) response.getBody()).getId());
        verify(userService).registerUser(request);
    }

}