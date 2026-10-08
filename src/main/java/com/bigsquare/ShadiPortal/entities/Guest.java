package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Integer id;

    private String name;

    private String phoneNumber;

    private String whatsapp_Number;

    private String email;

    private String guestCategory;

    private String gender;

    private String adultOrchild;

    private String gift;

    private String stay;

    private String cash;

    private Boolean invitationSent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    @JsonIgnoreProperties({"guestList", "user"})
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({
            "password",
            "guests",
            "families",
            "expenses"
    })
    private User user;

    @PrePersist
    public void applyDefaults() {
        if (invitationSent == null) {
            invitationSent = false;
        }
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", whatsapp_Number='" + whatsapp_Number + '\'' +
                ", email='" + email + '\'' +
                ", guestCategory='" + guestCategory + '\'' +
                ", gender='" + gender + '\'' +
                ", adultOrchild='" + adultOrchild + '\'' +
                ", gift='" + gift + '\'' +
                ", stay='" + stay + '\'' +
                ", cash='" + cash + '\'' +
                ", invitationSent=" + invitationSent +
                '}';
    }
}
