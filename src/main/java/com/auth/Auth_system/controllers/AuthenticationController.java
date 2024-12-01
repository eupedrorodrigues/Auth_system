package com.auth.Auth_system.controllers;


import com.auth.Auth_system.domain.user.AuthenticationDTO;
import com.auth.Auth_system.domain.user.LoginResponseDTO;
import com.auth.Auth_system.domain.user.RegisterDTO;
import com.auth.Auth_system.domain.user.User;
import com.auth.Auth_system.exceptions.EmailValidationException;
import com.auth.Auth_system.exceptions.NameValidationException;
import com.auth.Auth_system.exceptions.PasswordValidationException;
import com.auth.Auth_system.exceptions.UserAlreadyExistsException;
import com.auth.Auth_system.infra.security.TokenService;
import com.auth.Auth_system.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {

        validateName(data.name());
        validateEmail(data.login());
        validatePassword(data.password());

        if (repository.findByLogin(data.login()) != null) {
            throw new UserAlreadyExistsException("The email has already been registered.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.login(), encryptedPassword, data.role());

        repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new NameValidationException("Name cannot be empty or null.");
        }
        if (name.length() < 3) {
            throw new NameValidationException("Name must be at least 3 characters long.");
        }
    }

    private void validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new EmailValidationException("Invalid email format.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new PasswordValidationException("Password must be at least 8 characters long.");
        }
    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = repository.findAll();
        return ResponseEntity.ok(users);
    }
}
