package com.bigsquare.ShadiPortal.security;

import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    @Autowired
    private UserRepo userRepo;

    public String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null ||
                        !authentication.isAuthenticated() ||
                        "anonymousUser".equals(
                                authentication.getPrincipal()
                        )
        ) {

            throw new IllegalStateException(
                    "Authenticated user not found"
            );
        }

        return authentication.getName();
    }

    public User getCurrentUser() {

        String email =
                getCurrentUserEmail();

        return userRepo
                .findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Current user not found"
                        )
                );
    }

    public Integer getCurrentUserId() {

        return getCurrentUser()
                .getId();
    }

    /*
     * ROLE_USER:
     * Effective owner is the current user.
     *
     * ROLE_GUEST:
     * Effective owner is the linked ownerUser.
     */
    public User getCurrentOwnerUser() {

        User currentUser =
                getCurrentUser();

        if (
                "ROLE_GUEST".equals(
                        currentUser.getRole()
                )
        ) {

            User ownerUser =
                    currentUser.getOwnerUser();

            if (ownerUser == null) {

                throw new IllegalStateException(
                        "Guest account is not linked to a wedding owner"
                );
            }

            return ownerUser;
        }

        if (
                !"ROLE_USER".equals(
                        currentUser.getRole()
                )
        ) {

            throw new IllegalStateException(
                    "Unsupported user role"
            );
        }

        return currentUser;
    }

    public Integer getCurrentOwnerUserId() {

        return getCurrentOwnerUser()
                .getId();
    }

    public boolean isCurrentUserGuest() {

        return "ROLE_GUEST".equals(
                getCurrentUser()
                        .getRole()
        );
    }

    public boolean isCurrentUserOwner() {

        return "ROLE_USER".equals(
                getCurrentUser()
                        .getRole()
        );
    }
}
