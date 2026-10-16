package com.bigsquare.ShadiPortal.dto;

import com.bigsquare.ShadiPortal.enums.GiftType;
import lombok.*;


import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftDistributionRequest {

    private String recipientName;

    private String mobileNumber;

    private GiftType giftType;

    private BigDecimal amount;

    private Integer quantity;

    private String notes;
}
