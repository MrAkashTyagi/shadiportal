package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.LoginRequest;
import com.bigsquare.ShadiPortal.dto.LoginResponseDto;
import com.bigsquare.ShadiPortal.dto.RegisterRequest;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import com.bigsquare.ShadiPortal.dto.GuestAccountRequest;
import com.bigsquare.ShadiPortal.dto.GuestAccountResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

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

        user.setOwnerUser(
                null
        );

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

        String token =
                jwtUtil.generateToken(
                        user.getEmail()
                );

        return ResponseEntity.ok(

                new LoginResponseDto(
                        token,
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                )

        );
    }

    @PostMapping("/guest")
    public ResponseEntity<?>
    createGuestAccount(

            @Valid
            @RequestBody
            GuestAccountRequest request,

            Authentication authentication

    ) {

        String currentUserEmail =
                authentication.getName();

        User ownerUser =
                userRepo
                        .findByEmail(
                                currentUserEmail
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Owner user not found"
                                )
                        );

        if (
                !"ROLE_USER".equals(
                        ownerUser.getRole()
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            "Only wedding owners can create guest accounts"
                    );
        }

        String guestEmail =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (
                userRepo
                        .findByEmail(
                                guestEmail
                        )
                        .isPresent()
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Email already registered"
                    );
        }

        User guestUser =
                new User();

        guestUser.setName(
                request.getName()
                        .trim()
        );

        guestUser.setEmail(
                guestEmail
        );

        guestUser.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        guestUser.setRole(
                "ROLE_GUEST"
        );

        guestUser.setOwnerUser(
                ownerUser
        );

        guestUser.setAbout(
                "Wedding guest account"
        );

        guestUser.setImage(
                "default.png"
        );

        guestUser.setEnabled(
                true
        );

        User savedGuestUser =
                userRepo.save(
                        guestUser
                );

        GuestAccountResponse response =
                new GuestAccountResponse(
                        savedGuestUser.getId(),
                        savedGuestUser.getName(),
                        savedGuestUser.getEmail(),
                        savedGuestUser.getRole(),
                        ownerUser.getId()
                );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        response
                );
    }
}
