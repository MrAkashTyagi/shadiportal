package com.bigsquare.ShadiPortal.security;

import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {

        User user =
                userRepo.findByEmail(email)
                        .orElseThrow(
                                () -> new UsernameNotFoundException(
                                        "User not found"
                                )
                        );

        return org.springframework.security.core.userdetails.User
                .withUsername(
                        user.getEmail()
                )
                .password(
                        user.getPassword()
                )
                .authorities(
                        user.getRole()
                )
                .build();
    }
}
