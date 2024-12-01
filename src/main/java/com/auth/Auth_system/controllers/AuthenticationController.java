package com.auth.Auth_system.controllers;


import com.auth.Auth_system.domain.user.AuthenticationDTO;
import com.auth.Auth_system.domain.user.LoginResponseDTO;
import com.auth.Auth_system.domain.user.RegisterDTO;
import com.auth.Auth_system.domain.user.User;
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

        if (data.name() == null || data.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty or null.");
        }

        if(data.name().length() < 3){
            throw new IllegalArgumentException("Name must be at least 3 characters long.");
        }

        if (this.repository.findByLogin(data.login()) != null) {
            throw new UserAlreadyExistsException("The email has already been registered.");
        }

        if (data.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = repository.findAll();
        return ResponseEntity.ok(users);
    }
}
