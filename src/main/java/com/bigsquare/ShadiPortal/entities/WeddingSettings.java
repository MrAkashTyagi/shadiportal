package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wedding_settings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wedding_settings_user",
                        columnNames = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeddingSettings {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    @Column(length = 100)
    private String partnerName;

    @Column(nullable = false)
    private LocalDateTime weddingDateTime;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    @JsonIgnore
    private User user;

    private boolean configured;
}
