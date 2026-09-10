package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.LoginRequest;
import com.bigsquare.ShadiPortal.dto.RegisterRequest;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest request
    ) {

        if (
                userRepo.findByEmail(
                        request.getEmail()
                ).isPresent()
        ) {

            return ResponseEntity
                    .badRequest()
                    .body("Email already registered");
        }

        User user = new User();

        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPassword(
                request.getPassword()
        );

        user.setAbout(
                request.getAbout()
        );

        user.setRole("ROLE_USER");

        user.setEnabled(true);

        user.setImage("default.png");

        User savedUser =
                userRepo.save(user);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {

        Optional<User> userOptional =
                userRepo.findByEmail(
                        request.getEmail()
                );

        if (userOptional.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("User not found");
        }

        User user = userOptional.get();

        if (
                !user.getPassword()
                        .equals(request.getPassword())
        ) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid password");
        }

        return ResponseEntity.ok(user);
    }
}
