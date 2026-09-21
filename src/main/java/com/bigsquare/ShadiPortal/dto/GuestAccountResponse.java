package com.bigsquare.ShadiPortal.dto;

public class GuestAccountResponse {

    private Integer id;

    private String name;

    private String email;

    private String role;

    private Integer ownerUserId;

    public GuestAccountResponse(
            Integer id,
            String name,
            String email,
            String role,
            Integer ownerUserId
    ) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.ownerUserId = ownerUserId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Integer getOwnerUserId() {
        return ownerUserId;
    }
}
