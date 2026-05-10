package com.duoc.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest validRequest;

    private static final String USERNAME = "carlos";
    private static final String EMAIL = "carlos@mail.com";
    private static final String SECRET = "secret123";
    private static final String REQUIRED_FIELDS = "Username, email and password are required";
    private static final String HASHED_PASSWORD = "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1";
    private static final String WRONG_PASSWORD = "wrong";
    private static final String PLAINTEXT_PASSWORD = "plaintext";

    @BeforeEach
    void setUp() {
        validRequest = new UserRegistrationRequest();
        validRequest.setUsername(USERNAME);
        validRequest.setEmail(EMAIL);
        validRequest.setPassword(SECRET);
    }

    // ── registerUser ──────────────────────────────────────────────────────────

    @Test
    void registerUserShouldSaveAndReturnUserWhenRequestIsValid() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(SECRET)).thenReturn("$2a$10$hashedpassword1234567890123456789012345678901234567890ab");
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername(USERNAME);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(validRequest);

        assertEquals(savedUser, result);
        verify(passwordEncoder).encode(SECRET);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserShouldTrimUsernameAndEmail() {
        validRequest.setUsername("  "+USERNAME+"  ");
        validRequest.setEmail("  "+EMAIL+"  ");
        when(userRepository.existsByUsername("  "+USERNAME+"  ")).thenReturn(false);
        when(userRepository.existsByEmail("  "+EMAIL+"  ")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(validRequest);

        assertEquals(USERNAME, result.getUsername());
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void registerUserShouldThrowWhenRequestIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(null));
        assertEquals("Request body is required", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserShouldThrowWhenUsernameIsBlank() {
        validRequest.setUsername("  ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validRequest));
        assertEquals(REQUIRED_FIELDS, ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserShouldThrowWhenEmailIsBlank() {
        validRequest.setEmail("");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validRequest));
        assertEquals(REQUIRED_FIELDS, ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserShouldThrowWhenPasswordIsNull() {
        validRequest.setPassword(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validRequest));
        assertEquals(REQUIRED_FIELDS, ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserShouldThrowWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validRequest));
        assertEquals("Username already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserShouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validRequest));
        assertEquals("Email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ── authenticate ──────────────────────────────────────────────────────────

    @Test
    void authenticateShouldReturnTrueWhenHashedPasswordMatches() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(HASHED_PASSWORD);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(passwordEncoder.matches(SECRET, user.getPassword())).thenReturn(true);

        assertTrue(userService.authenticate(USERNAME, SECRET));
        verify(passwordEncoder).matches(SECRET, user.getPassword());
    }

    @Test
    void authenticateShouldReturnFalseWhenHashedPasswordDoesNotMatch() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(HASHED_PASSWORD);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(passwordEncoder.matches(WRONG_PASSWORD, user.getPassword())).thenReturn(false);

        assertFalse(userService.authenticate(USERNAME, WRONG_PASSWORD));
        verify(passwordEncoder).matches(WRONG_PASSWORD, user.getPassword());
    }

    @Test
    void authenticateShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertFalse(userService.authenticate("unknown", SECRET));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticateShouldReturnFalseWhenRawPasswordIsBlank() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PLAINTEXT_PASSWORD);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        assertFalse(userService.authenticate(USERNAME, "  "));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticateShouldReturnFalseWhenStoredPasswordIsBlank() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword("");
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        assertFalse(userService.authenticate(USERNAME, SECRET));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticateShouldReturnFalseWhenPlainPasswordDoesNotMatch() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword("correctplain");
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        assertFalse(userService.authenticate(USERNAME, "wrongplain"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticateShouldMigratePlainPasswordOnSuccessfulMatch() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PLAINTEXT_PASSWORD);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(passwordEncoder.encode(PLAINTEXT_PASSWORD)).thenReturn(HASHED_PASSWORD);

        assertTrue(userService.authenticate(USERNAME, PLAINTEXT_PASSWORD));
        verify(passwordEncoder).encode(PLAINTEXT_PASSWORD);
        verify(userRepository).save(user);
    }

    // ── migratePlainTextPasswords ─────────────────────────────────────────────

    @Test
    void migratePlainTextPasswordsShouldMigrateAllPlainUsers() {
        User plain1 = new User();
        plain1.setPassword("pass1");
        User plain2 = new User();
        plain2.setPassword("pass2");
        when(userRepository.findAll()).thenReturn(List.of(plain1, plain2));
        when(passwordEncoder.encode("pass1")).thenReturn(HASHED_PASSWORD);
        when(passwordEncoder.encode("pass2")).thenReturn(HASHED_PASSWORD);

        int migrated = userService.migratePlainTextPasswords();

        assertEquals(2, migrated);
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void migratePlainTextPasswordsShouldSkipAlreadyHashedPasswords() {
        User hashed = new User();
        hashed.setPassword(HASHED_PASSWORD);
        when(userRepository.findAll()).thenReturn(List.of(hashed));

        int migrated = userService.migratePlainTextPasswords();

        assertEquals(0, migrated);
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void migratePlainTextPasswordsShouldSkipUsersWithBlankPassword() {
        User blank = new User();
        blank.setPassword("  ");
        when(userRepository.findAll()).thenReturn(List.of(blank));

        int migrated = userService.migratePlainTextPasswords();

        assertEquals(0, migrated);
        verify(userRepository, never()).save(any());
    }

    @Test
    void migratePlainTextPasswordsShouldReturnZeroWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        int migrated = userService.migratePlainTextPasswords();

        assertEquals(0, migrated);
    }

    // ── isPasswordHashed ──────────────────────────────────────────────────────

    @Test
    void isPasswordHashedShouldReturnTrueForValidBcryptHash() {
        assertTrue(userService.isPasswordHashed(HASHED_PASSWORD));
    }

    @Test
    void isPasswordHashedShouldReturnFalseForPlainText() {
        assertFalse(userService.isPasswordHashed(PLAINTEXT_PASSWORD));
    }

    @Test
    void isPasswordHashedShouldReturnFalseForNull() {
        assertFalse(userService.isPasswordHashed(null));
    }
}
