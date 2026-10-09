package com.bigsquare.ShadiPortal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeddingContactResponse {

    private Long id;

    private String name;

    private String category;

    private String contactPerson;

    private String mobileNumber;

    private String alternateNumber;

    private String whatsappNumber;

    private String address;

    private String mapLink;

    private String notes;

    private Double advancePaid;

    private Double pendingAmount;
}
