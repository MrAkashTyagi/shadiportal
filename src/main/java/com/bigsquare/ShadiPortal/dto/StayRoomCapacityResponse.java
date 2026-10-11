package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StayRoomCapacityResponse {

    private Integer roomId;

    private String roomName;

    private Integer locationId;

    private String locationName;

    private Integer capacity;

    private Long occupiedBeds;

    private Long availableBeds;

    private Boolean full;
}
