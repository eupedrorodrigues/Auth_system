package com.auth.Auth_system.controllers;

import com.auth.Auth_system.domain.user.*;
import com.auth.Auth_system.exceptions.UserAlreadyExistsException;
import com.auth.Auth_system.repositories.UserRepository;
import com.auth.Auth_system.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository repository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationController authenticationController;


    @Test
    void testLogin() {

        String login = "test@example.com";
        String password = "password123";
        AuthenticationDTO authDTO = new AuthenticationDTO(login, password);

        User mockUser = mock(User.class);
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        when(tokenService.generateToken(mockUser)).thenReturn("generated-token");

        ResponseEntity<LoginResponseDTO> response = authenticationController.login(authDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().token());
    }


    @Test
    void testRegisterUserAlreadyExists() {

        String login = "existing@example.com";
        RegisterDTO registerDTO = new RegisterDTO("Test User", login, "password123", UserRole.USER);

        User mockUser = new User("Test User", login, "password123", UserRole.USER);
        when(repository.findByLogin(login)).thenReturn(mockUser);

        assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationController.register(registerDTO);
        });
    }


    @Test
    void testRegisterNewUser() {

        String login = "newuser@example.com";
        RegisterDTO registerDTO = new RegisterDTO("New User", login, "password123", UserRole.USER);

        when(repository.findByLogin(login)).thenReturn(null);

        ResponseEntity<Void> response = authenticationController.register(registerDTO);

        verify(repository, times(1)).save(any(User.class));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetAllUsers() {

        List<User> users = List.of(new User("Test User", "test@example.com", "password", UserRole.USER));
        when(repository.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = authenticationController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testNameNotEmpty() {
        String login = "test@example.com";
        String validPassword = "password123";
        String nullName = null;
        String emptyName = "";
        String validName = "Test User";

        RegisterDTO nullNameDTO = new RegisterDTO(nullName, login, validPassword, UserRole.USER);
        RegisterDTO emptyNameDTO = new RegisterDTO(emptyName, login, validPassword, UserRole.USER);
        RegisterDTO validNameDTO = new RegisterDTO(validName, login, validPassword, UserRole.USER);

        assertThrows(IllegalArgumentException.class, () -> authenticationController.register(nullNameDTO));

        assertThrows(IllegalArgumentException.class, () -> authenticationController.register(emptyNameDTO));

        assertDoesNotThrow(() -> authenticationController.register(validNameDTO));
    }

    @Test
    void testNameLength() {
        String login = "test@example.com";
        String password = "password123";
        String shortName = "Jo";

        RegisterDTO shortNameDTO = new RegisterDTO(shortName, login, password, UserRole.USER);

        assertThrows(IllegalArgumentException.class, () -> authenticationController.register(shortNameDTO));
    }


    @Test
    void testPasswordLength() {
        String login = "test@example.com";
        String shortPassword = "pass";
        String validPassword = "password123";

        RegisterDTO shortPasswordDTO = new RegisterDTO("Test User", login, shortPassword, UserRole.USER);
        RegisterDTO validPasswordDTO = new RegisterDTO("Test User", login, validPassword, UserRole.USER);

        assertThrows(IllegalArgumentException.class, () -> authenticationController.register(shortPasswordDTO));

        assertDoesNotThrow(() -> authenticationController.register(validPasswordDTO));
    }


    @Test
    void testLoginWithInvalidCredentials() {
        String login = "test@example.com";
        String invalidPassword = "wrongpassword";
        AuthenticationDTO authDTO = new AuthenticationDTO(login, invalidPassword);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(authDTO));
    }

    @Test
    void testValidRegister() {
        String login = "validuser@example.com";
        String validPassword = "password123";
        String validName = "Valid User";

        RegisterDTO validRegisterDTO = new RegisterDTO(validName, login, validPassword, UserRole.USER);

        when(repository.findByLogin(login)).thenReturn(null);

        ResponseEntity<Void> response = authenticationController.register(validRegisterDTO);

        verify(repository, times(1)).save(any(User.class));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testLoginWithNonExistentEmail() {
        String nonExistentLogin = "nonexistent@example.com";
        String password = "password123";
        AuthenticationDTO authDTO = new AuthenticationDTO(nonExistentLogin, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(authDTO));
    }



    @Test
    void testEmailFormat() {
        String invalidEmail = "invalid-email";
        String validEmail = "valid@example.com";
        String password = "password123";

        RegisterDTO invalidEmailDTO = new RegisterDTO("Test User", invalidEmail, password, UserRole.USER);
        RegisterDTO validEmailDTO = new RegisterDTO("Test User", validEmail, password, UserRole.USER);

        assertThrows(IllegalArgumentException.class, () -> authenticationController.register(invalidEmailDTO));
        assertDoesNotThrow(() -> authenticationController.register(validEmailDTO));
    }


    @Test
    void testPasswordEncryption() {
        String login = "test@example.com";
        String password = "password123";
        RegisterDTO registerDTO = new RegisterDTO("Test User", login, password, UserRole.USER);

        when(repository.findByLogin(login)).thenReturn(null);
        when(repository.save(any(User.class))).thenReturn(new User("Test User", login, "encrypted-password", UserRole.USER));

        ResponseEntity<Void> response = authenticationController.register(registerDTO);

        verify(repository, times(1)).save(any(User.class));
        assertEquals(200, response.getStatusCodeValue());
    }


}
