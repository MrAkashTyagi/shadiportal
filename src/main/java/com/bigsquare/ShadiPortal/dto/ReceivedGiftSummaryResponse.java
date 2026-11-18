package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedGiftSummaryResponse {

    private Long totalRecords;

    private Long totalQuantity;

    private Long cashGiftRecords;

    private BigDecimal totalCashReceived;

    private Long physicalGiftQuantity;
}
