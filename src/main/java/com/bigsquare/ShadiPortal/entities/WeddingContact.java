package com.bigsquare.ShadiPortal.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wedding_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeddingContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userId;

    private String name;

    private String category;

    private String contactPerson;

    private String mobileNumber;

    private String alternateNumber;

    private String whatsappNumber;

    @Column(length = 1000)
    private String address;

    @Column(length = 1000)
    private String mapLink;

    @Column(length = 2000)
    private String notes;

    private Double advancePaid;

    private Double pendingAmount;
}
