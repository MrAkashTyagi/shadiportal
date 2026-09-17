package com.bigsquare.ShadiPortal.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService
            customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader(
                        "Authorization"
                );

        if (
                authorizationHeader == null
                        || !authorizationHeader.startsWith(
                        "Bearer "
                )
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authorizationHeader.substring(
                        7
                );

        try {

            String email =
                    jwtUtil.extractEmail(
                            token
                    );

            System.out.println(

                    "JWT Email = " + email

            );

            if (
                    email != null
                            && SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            == null
            ) {

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(
                                        email
                                );

                if (
                        jwtUtil.validateToken(
                                token,
                                userDetails.getUsername()
                        )
                ) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(
                                            request
                                    )
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );
                }


                boolean valid =
                        jwtUtil.validateToken(
                                token,
                                userDetails.getUsername()
                        );

                System.out.println(
                        "JWT Valid = " + valid
                );

                if (valid) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "JWT Authentication Set = "
                                    + authentication.getName()
                    );
                }

            }

        } catch (Exception exception) {

        System.out.println(
                "JWT Authentication Failed: "
                        + exception.getClass().getSimpleName()
                        + " - "
                        + exception.getMessage()
        );

        SecurityContextHolder.clearContext();
    }

        filterChain.doFilter(
                request,
                response
        );
    }
}
