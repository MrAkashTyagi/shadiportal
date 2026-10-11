package com.bigsquare.ShadiPortal.dto;

import com.bigsquare.ShadiPortal.enums.StayAllocationStatus;
import com.bigsquare.ShadiPortal.enums.StayLocationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StayAllocationResponse {

    private Integer id;

    private Integer guestId;

    private String guestName;

    private Integer familyId;

    private String familyName;

    private Integer roomId;

    private String roomName;

    private Integer roomCapacity;

    private Integer locationId;

    private String locationName;

    private StayLocationType locationType;

    private StayAllocationStatus status;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
