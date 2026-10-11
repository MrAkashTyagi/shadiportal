package com.bigsquare.ShadiPortal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkStayAllocationRequest {

    private List<Integer> guestIds;

    private Integer roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String notes;
}
