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
public class GiftEnvelopeSummaryItem {

    private BigDecimal amount;

    private Long count;

    private BigDecimal totalAmount;
}
