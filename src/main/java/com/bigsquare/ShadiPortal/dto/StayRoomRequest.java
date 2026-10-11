package com.bigsquare.ShadiPortal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StayRoomRequest {

    private Integer locationId;

    private String name;

    private Integer capacity;

    private String floor;

    private String notes;

    private Boolean active;
}
