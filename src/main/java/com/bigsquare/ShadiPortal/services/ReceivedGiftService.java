package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.ReceivedGiftRequest;
import com.bigsquare.ShadiPortal.dto.ReceivedGiftResponse;
import com.bigsquare.ShadiPortal.dto.ReceivedGiftSummaryResponse;
import com.bigsquare.ShadiPortal.enums.ReceivedGiftType;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ReceivedGiftService {

    ReceivedGiftResponse create(
            ReceivedGiftRequest request
    );

    ReceivedGiftResponse update(
            Integer id,
            ReceivedGiftRequest request
    );

    ReceivedGiftResponse getById(
            Integer id
    );

    Page<ReceivedGiftResponse> getAll(
            int page,
            int size,
            String search,
            ReceivedGiftType giftType,
            BigDecimal minCashAmount,
            BigDecimal maxCashAmount
    );

    void delete(
            Integer id
    );

    ReceivedGiftSummaryResponse getSummary();
}
