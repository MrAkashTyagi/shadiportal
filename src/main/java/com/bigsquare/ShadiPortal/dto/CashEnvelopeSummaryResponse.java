package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashEnvelopeSummaryResponse {

    private List<CashEnvelopeSummaryItem> items;

    private Long totalEnvelopes;

    private Long grandTotal;
}
