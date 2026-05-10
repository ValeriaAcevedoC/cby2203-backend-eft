package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginController loginController;

    private static final String CARLOS = "carlos";
    private static final String SECRET = "secret";
    private static final String WRONG = "wrong";

    @Test
    void loginShouldReturnOkWithTokenWhenCredentialsAreValid() {
        LoginRequest loginRequest = new LoginRequest(CARLOS, SECRET);
        when(userService.authenticate(CARLOS, SECRET)).thenReturn(true);
        when(jwtAuthenticationConfig.getJWTToken(CARLOS)).thenReturn("jwt-token");

        ResponseEntity<String> response = loginController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
        verify(userService).authenticate(CARLOS, SECRET);
        verify(jwtAuthenticationConfig).getJWTToken(CARLOS);
    }

    @Test
    void loginShouldReturnBadRequestWhenCredentialsAreInvalid() {
        LoginRequest loginRequest = new LoginRequest(CARLOS, WRONG);
        when(userService.authenticate(CARLOS, WRONG)).thenReturn(false);

        ResponseEntity<String> response = loginController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
        verify(userService).authenticate(CARLOS, WRONG);
        verify(jwtAuthenticationConfig, never()).getJWTToken(CARLOS);
    }

    @Test
    void loginShouldReturnBadRequestWhenAuthenticationThrowsException() {
        LoginRequest loginRequest = new LoginRequest(CARLOS, SECRET);
        when(userService.authenticate(CARLOS, SECRET)).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<String> response = loginController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Login failed: DB down", response.getBody());
        verify(userService).authenticate(CARLOS, SECRET);
    }

    @Test
    void loginShouldReturnBadRequestWhenTokenGenerationThrowsException() {
        LoginRequest loginRequest = new LoginRequest(CARLOS, SECRET);
        when(userService.authenticate(CARLOS, SECRET)).thenReturn(true);
        when(jwtAuthenticationConfig.getJWTToken(CARLOS)).thenThrow(new RuntimeException("JWT error"));

        ResponseEntity<String> response = loginController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Login failed: JWT error", response.getBody());
        verify(userService).authenticate(CARLOS, SECRET);
        verify(jwtAuthenticationConfig).getJWTToken(CARLOS);
    }
}
