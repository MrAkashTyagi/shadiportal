package com.bigsquare.ShadiPortal.dto;

import com.bigsquare.ShadiPortal.enums.GiftType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftDistributionResponse {

    private Integer id;

    private String recipientName;

    private String mobileNumber;

    private GiftType giftType;

    private BigDecimal amount;

    private Integer quantity;

    private BigDecimal totalAmount;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
