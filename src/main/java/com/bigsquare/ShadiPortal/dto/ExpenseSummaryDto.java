package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryDto {

    private Double totalExpense;

    private Double totalPaidExpense;

    private Double totalPendingExpense;

    private Long totalExpenses;

    private Double highestExpense;

    private String topCategory;

}
