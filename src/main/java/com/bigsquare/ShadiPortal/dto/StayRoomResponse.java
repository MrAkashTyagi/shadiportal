package com.bigsquare.ShadiPortal.dto;

import com.bigsquare.ShadiPortal.enums.StayLocationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StayRoomResponse {

    private Integer id;

    private String name;

    private Integer capacity;

    private String floor;

    private String notes;

    private Boolean active;

    private Integer locationId;

    private String locationName;

    private StayLocationType locationType;
}
