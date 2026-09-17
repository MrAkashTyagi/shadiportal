package com.bigsquare.ShadiPortal.security;

import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepo userRepo;

    public CurrentUserService(
            UserRepo userRepo
    ) {
        this.userRepo = userRepo;
    }

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null
                        || !authentication.isAuthenticated()
                        || "anonymousUser".equals(
                        authentication.getPrincipal()
                )
        ) {

            throw new IllegalStateException(
                    "Authenticated user is not available"
            );
        }

        String email =
                authentication.getName();

        return userRepo.findByEmail(
                        email
                )
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    public Integer getCurrentUserId() {

        return getCurrentUser().getId();
    }
}
