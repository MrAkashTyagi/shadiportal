package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.GiftDistributionRequest;
import com.bigsquare.ShadiPortal.dto.GiftDistributionResponse;
import com.bigsquare.ShadiPortal.dto.GiftDistributionSummaryResponse;
import com.bigsquare.ShadiPortal.enums.GiftType;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface GiftDistributionService {

    GiftDistributionResponse create(
            GiftDistributionRequest request
    );

    GiftDistributionResponse update(
            Integer id,
            GiftDistributionRequest request
    );

    GiftDistributionResponse getById(
            Integer id
    );

    Page<GiftDistributionResponse> getAll(
            int page,
            int size,
            String search,
            GiftType giftType,
            BigDecimal minAmount,
            BigDecimal maxAmount
    );

    void delete(
            Integer id
    );

    GiftDistributionSummaryResponse getSummary();

    byte[] export(
            String search,
            GiftType giftType,
            BigDecimal minAmount,
            BigDecimal maxAmount
    );

}
