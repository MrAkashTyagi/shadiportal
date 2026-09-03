package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategorySummaryDto {

    private String category;
    private BigDecimal totalAmount;

}
