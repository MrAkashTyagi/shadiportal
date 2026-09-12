package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.dto.GuestCategorySummaryDto;
import com.bigsquare.ShadiPortal.entities.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepo extends JpaRepository<Guest, Integer> {

//    @Query("SELECT g FROM Guest g WHERE " +
//            "LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//            "CAST(g.id AS string) LIKE CONCAT('%', :search, '%')")
//    Page<Guest> findBySearchQuery(@Param("search") String search, Pageable pageable);


    @Query("""
            SELECT g
            FROM Guest g
            WHERE
                        g.user.id = :userId
                                    
                AND (
                    :search = ''
                    OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR CAST(g.id AS string) LIKE CONCAT('%', :search, '%')
                )
                AND
                (
                    :gender = ''
                    OR LOWER(g.gender) = LOWER(:gender)
                )
                        AND
                        (
                            :adultOrchild = ''
                            OR LOWER(g.adultOrchild) = LOWER(:adultOrchild)
                        )
                                 AND
                        (
                            :guestCategory = ''
                            OR LOWER(g.guestCategory) = LOWER(:guestCategory)
                        )
                                AND
                        (
                            :gift = ''
                            OR LOWER(g.gift) = LOWER(:gift)
                        )
                                         AND
                        (
                            :stay = ''
                            OR LOWER(g.stay) = LOWER(:stay)
                        )
                                                AND
                        (
                            :cash = ''
                            OR LOWER(g.cash) = LOWER(:cash)
                        )
                               AND (
                                   :invitationSent IS NULL
                                   OR
                                   (
                                       :invitationSent = true
                                       AND g.invitationSent = true
                                   )
                                   OR
                                   (
                                       :invitationSent = false
                                       AND (
                                           g.invitationSent = false
                                           OR g.invitationSent IS NULL
                                       )
                                   )
                               )
            """)
    Page<Guest> findGuestsWithFilters(
            @Param("userId") Integer userId,
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("adultOrchild") String adultOrchild,
            @Param("guestCategory") String guestCategory,
            @Param("gift") String gift,
            @Param("stay") String stay,
            @Param("cash") String cash,
            @Param("invitationSent") Boolean invitationSent,
            Pageable pageable
    );

    @Query("""
        SELECT g
        FROM Guest g
        WHERE
            g.user.id = :userId

            AND (
                :search = ''
                OR LOWER(g.name)
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR CAST(g.id AS string)
                    LIKE CONCAT('%', :search, '%')
            )

            AND (
                :gender = ''
                OR LOWER(g.gender) = LOWER(:gender)
            )

            AND (
                :adultOrchild = ''
                OR LOWER(g.adultOrchild) = LOWER(:adultOrchild)
            )

            AND (
                :guestCategory = ''
                OR LOWER(g.guestCategory) = LOWER(:guestCategory)
            )

            AND (
                :gift = ''
                OR LOWER(g.gift) = LOWER(:gift)
            )

            AND (
                :stay = ''
                OR LOWER(g.stay) = LOWER(:stay)
            )

            AND (
                :cash = ''
                OR LOWER(g.cash) = LOWER(:cash)
            )

            AND (
                :invitationSent IS NULL

                OR (
                    :invitationSent = true
                    AND g.invitationSent = true
                )

                OR (
                    :invitationSent = false
                    AND (
                        g.invitationSent = false
                        OR g.invitationSent IS NULL
                    )
                )
            )

        ORDER BY g.id ASC
        """)
    List<Guest> findAllGuestsWithFilters(
            @Param("userId") Integer userId,
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("adultOrchild") String adultOrchild,
            @Param("guestCategory") String guestCategory,
            @Param("gift") String gift,
            @Param("stay") String stay,
            @Param("cash") String cash,
            @Param("invitationSent") Boolean invitationSent
    );

    long countByInvitationSentTrue();

    @Query("""
            SELECT COUNT(g)
            FROM Guest g
            WHERE g.invitationSent = false
            OR g.invitationSent IS NULL
            """)
    long countPendingInvitations();

    long countByStay(String stay);

    @Query("""
            SELECT new com.bigsquare.ShadiPortal.dto.GuestCategorySummaryDto(
                COALESCE(g.guestCategory, 'Not Set'),
                COUNT(g)
            )
            FROM Guest g
            GROUP BY g.guestCategory
            ORDER BY COUNT(g) DESC
            """)
    List<GuestCategorySummaryDto> getGuestCategorySummary();

    @Query("""
                SELECT COUNT(g)
                FROM Guest g
                WHERE g.family IS NOT NULL
            """)
    Long countGuestsHavingFamily();

    Page<Guest> findAllByOrderByIdDesc(
            Pageable pageable
    );

    Optional<Guest> findByIdAndUserId(
            Integer guestId,
            Integer userId
    );

    List<Guest> findAllByUserId(
            Integer userId
    );

    long countByUserId(
            Integer userId
    );

    Page<Guest> findByUserIdOrderByIdDesc(
            Long userId,
            Pageable pageable);

//    long countByUserIdAndInvitationSentTrue(
//            Integer userId
//    );

    long countByUserIdAndStayIgnoreCase(
            Integer userId,
            String stay
    );

    Page<Guest> findAllByUserId(
            Integer userId,
            Pageable pageable
    );

    List<Guest> findByUserId(
            Integer userId
    );

    Long countByUserIdAndInvitationSentTrue(
            Integer userId
    );

    @Query("""
       SELECT COUNT(g)
       FROM Guest g
       WHERE g.user.id = :userId
       AND g.invitationSent = false
       """)
    Long countPendingInvitationsByUserId(
            @Param("userId") Integer userId
    );

    Long countByUserIdAndStay(
            Integer userId,
            String stay
    );

}
