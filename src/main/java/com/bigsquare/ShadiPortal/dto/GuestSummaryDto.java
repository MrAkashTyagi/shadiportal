package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuestSummaryDto {

    private Long totalGuests;
    private Long invitationSent;
    private Long invitationPending;
    private Long stayRequired;

}
