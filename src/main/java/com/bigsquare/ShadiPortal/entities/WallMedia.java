package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wall_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WallMedia {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(
            nullable = false,
            unique = true
    )
    private String storedFileName;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WallMediaType mediaType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 500)
    private String caption;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    /*
     * Existing user_id column is now treated as the
     * wedding-space owner.
     *
     * This preserves existing Wall media records.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    @JsonIgnoreProperties({
            "password",
            "guests",
            "expenses",
            "ownerUser"
    })
    private User ownerUser;

    /*
     * Actual account that uploaded the file.
     *
     * Nullable temporarily, because older Wall records
     * will not have this value until database backfill.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "uploaded_by_user_id"
    )
    @JsonIgnoreProperties({
            "password",
            "guests",
            "expenses",
            "ownerUser"
    })
    private User uploadedBy;
}
