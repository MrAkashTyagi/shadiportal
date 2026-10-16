package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.GiftDistribution;
import com.bigsquare.ShadiPortal.enums.GiftType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface GiftDistributionRepository
        extends JpaRepository<GiftDistribution, Integer> {

    Optional<GiftDistribution> findByIdAndUserId(
            Integer giftDistributionId,
            Integer userId
    );

    @Query("""
            SELECT gift
            FROM GiftDistribution gift
            WHERE gift.user.id = :userId
            AND (
                    :search = ''
                    OR LOWER(gift.recipientName)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(gift.mobileNumber, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(gift.notes, ''))
                        LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (
                    :giftType IS NULL
                    OR gift.giftType = :giftType
            )
            AND (
                    :minAmount IS NULL
                    OR gift.amount >= :minAmount
            )
            AND (
                    :maxAmount IS NULL
                    OR gift.amount <= :maxAmount
            )
            """)
    Page<GiftDistribution> findWithFilters(
            @Param("userId")
            Integer userId,

            @Param("search")
            String search,

            @Param("giftType")
            GiftType giftType,

            @Param("minAmount")
            BigDecimal minAmount,

            @Param("maxAmount")
            BigDecimal maxAmount,

            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(
                    SUM(gift.amount * gift.quantity),
                    0
            )
            FROM GiftDistribution gift
            WHERE gift.user.id = :userId
            """)
    BigDecimal getTotalAmountByUserId(
            @Param("userId")
            Integer userId
    );

    @Query("""
            SELECT COALESCE(
                    SUM(gift.quantity),
                    0
            )
            FROM GiftDistribution gift
            WHERE gift.user.id = :userId
            """)
    Long getTotalQuantityByUserId(
            @Param("userId")
            Integer userId
    );

    long countByUserId(
            Integer userId
    );
}
