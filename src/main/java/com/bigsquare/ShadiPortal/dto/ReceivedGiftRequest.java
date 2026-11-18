package com.bigsquare.ShadiPortal.dto;

import com.bigsquare.ShadiPortal.enums.ReceivedGiftType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedGiftRequest {

    private String giverName;

    private String mobileNumber;

    private ReceivedGiftType giftType;

    private BigDecimal cashAmount;

    private String giftDescription;

    private Integer quantity;

    private String notes;

    private LocalDateTime receivedAt;
}
