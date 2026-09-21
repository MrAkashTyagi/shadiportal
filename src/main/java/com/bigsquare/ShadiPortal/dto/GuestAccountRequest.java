package com.bigsquare.ShadiPortal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GuestAccountRequest {

    @NotBlank(
            message = "Guest name is required"
    )
    @Size(
            min = 2,
            max = 50,
            message = "Guest name must be between 2 and 50 characters"
    )
    private String name;

    @NotBlank(
            message = "Guest email is required"
    )
    @Email(
            message = "Please enter a valid email"
    )
    private String email;

    @NotBlank(
            message = "Guest password is required"
    )
    @Size(
            min = 6,
            message = "Password must contain at least 6 characters"
    )
    private String password;

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }
}
