package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.ReceivedGift;
import com.bigsquare.ShadiPortal.enums.ReceivedGiftType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ReceivedGiftRepository
        extends JpaRepository<ReceivedGift, Integer> {

    Optional<ReceivedGift> findByIdAndUserId(
            Integer receivedGiftId,
            Integer userId
    );

    @Query("""
            SELECT gift
            FROM ReceivedGift gift
            WHERE gift.user.id = :userId

            AND (
                :search = ''
                OR LOWER(gift.giverName)
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(gift.mobileNumber, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(gift.giftDescription, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(gift.notes, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
            )

            AND (
                :giftType IS NULL
                OR gift.giftType = :giftType
            )

            AND (
                :minCashAmount IS NULL
                OR (
                    gift.giftType =
                        com.bigsquare.ShadiPortal.enums.ReceivedGiftType.CASH
                    AND gift.cashAmount >= :minCashAmount
                )
            )

            AND (
                :maxCashAmount IS NULL
                OR (
                    gift.giftType =
                        com.bigsquare.ShadiPortal.enums.ReceivedGiftType.CASH
                    AND gift.cashAmount <= :maxCashAmount
                )
            )
            """)
    Page<ReceivedGift> findWithFilters(
            @Param("userId")
            Integer userId,

            @Param("search")
            String search,

            @Param("giftType")
            ReceivedGiftType giftType,

            @Param("minCashAmount")
            BigDecimal minCashAmount,

            @Param("maxCashAmount")
            BigDecimal maxCashAmount,

            Pageable pageable
    );

    long countByUserId(
            Integer userId
    );

    @Query("""
            SELECT COALESCE(
                SUM(gift.quantity),
                0
            )
            FROM ReceivedGift gift
            WHERE gift.user.id = :userId
            """)
    Long getTotalQuantityByUserId(
            @Param("userId")
            Integer userId
    );

    @Query("""
            SELECT COUNT(gift)
            FROM ReceivedGift gift
            WHERE gift.user.id = :userId
              AND gift.giftType =
                  com.bigsquare.ShadiPortal.enums.ReceivedGiftType.CASH
            """)
    Long countCashGiftsByUserId(
            @Param("userId")
            Integer userId
    );

    @Query("""
            SELECT COALESCE(
                SUM(gift.cashAmount * gift.quantity),
                0
            )
            FROM ReceivedGift gift
            WHERE gift.user.id = :userId
              AND gift.giftType =
                  com.bigsquare.ShadiPortal.enums.ReceivedGiftType.CASH
            """)
    BigDecimal getTotalCashReceivedByUserId(
            @Param("userId")
            Integer userId
    );

    @Query("""
            SELECT COALESCE(
                SUM(gift.quantity),
                0
            )
            FROM ReceivedGift gift
            WHERE gift.user.id = :userId
              AND gift.giftType <>
                  com.bigsquare.ShadiPortal.enums.ReceivedGiftType.CASH
            """)
    Long getPhysicalGiftQuantityByUserId(
            @Param("userId")
            Integer userId
    );
}
