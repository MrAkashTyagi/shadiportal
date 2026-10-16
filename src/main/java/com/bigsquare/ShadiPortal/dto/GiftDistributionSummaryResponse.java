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
public class GiftDistributionSummaryResponse {

    private Long totalRecords;

    private Long totalQuantity;

    private BigDecimal totalAmount;
}
