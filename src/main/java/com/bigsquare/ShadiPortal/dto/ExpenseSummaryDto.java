package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryDto {

    private BigDecimal totalExpense;
    private Long totalExpenses;
    private BigDecimal highestExpense;
    private String topCategory;

}
