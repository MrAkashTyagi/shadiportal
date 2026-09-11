package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.LoginRequest;
import com.bigsquare.ShadiPortal.dto.LoginResponseDto;
import com.bigsquare.ShadiPortal.dto.RegisterRequest;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

//    register

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
                passwordEncoder.encode(
                        request.getPassword()
                )
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

//    login

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
                !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                )
        ) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid password");
        }


        System.out.println(
                "Email : " + request.getEmail()
        );

        System.out.println(
                "Password From UI : "
                        + request.getPassword()
        );

        System.out.println(
                "Password From DB : "
                        + user.getPassword()
        );

        System.out.println(
                "Password Match : "
                        + passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                )
        );

        System.out.println("LOGIN SUCCESS");




        return ResponseEntity.ok(

                new LoginResponseDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )

        );
    }
}
