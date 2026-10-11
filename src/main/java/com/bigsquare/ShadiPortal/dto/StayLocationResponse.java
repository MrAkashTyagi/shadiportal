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
public class StayLocationResponse {

    private Integer id;

    private String name;

    private StayLocationType type;

    private String address;

    private String contactPerson;

    private String contactNumber;

    private String notes;

    private Boolean active;
}
