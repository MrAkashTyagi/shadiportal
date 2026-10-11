package com.bigsquare.ShadiPortal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "stay_room",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stay_room_location_name",
                        columnNames = {
                                "stay_location_id",
                                "name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StayRoom {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "stay_room_id"
    )
    private Integer id;

    @Column(
            nullable = false,
            length = 120
    )
    private String name;

    @Column(
            nullable = false
    )
    private Integer capacity;

    @Column(
            length = 50
    )
    private String floor;

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
            name = "stay_location_id",
            nullable = false
    )
    private StayLocation location;

    @PrePersist
    public void applyDefaults() {

        if (active == null) {
            active = true;
        }
    }
}
