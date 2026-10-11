package com.bigsquare.ShadiPortal.entities;

import com.bigsquare.ShadiPortal.enums.StayLocationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "stay_location",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stay_location_user_name",
                        columnNames = {
                                "user_id",
                                "name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StayLocation {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "stay_location_id"
    )
    private Integer id;

    @Column(
            nullable = false,
            length = 120
    )
    private String name;

    @Enumerated(
            EnumType.STRING
    )
    @Column(
            nullable = false,
            length = 30
    )
    private StayLocationType type;

    @Column(
            length = 500
    )
    private String address;

    @Column(
            name = "contact_person",
            length = 120
    )
    private String contactPerson;

    @Column(
            name = "contact_number",
            length = 20
    )
    private String contactNumber;

    @Column(
            length = 1000
    )
    private String notes;

    @Column(
            nullable = false
    )
    private Boolean active = true;

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
            "families",
            "expenses"
    })
    private User user;

    @PrePersist
    public void applyDefaults() {

        if (active == null) {
            active = true;
        }
    }
}
