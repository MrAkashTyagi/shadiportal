package com.bigsquare.ShadiPortal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StayAllocationRequest {

    private Integer guestId;

    private Integer roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String notes;
}
