package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepo extends JpaRepository<Guest, Integer>
{

//    @Query("SELECT g FROM Guest g WHERE " +
//            "LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//            "CAST(g.id AS string) LIKE CONCAT('%', :search, '%')")
//    Page<Guest> findBySearchQuery(@Param("search") String search, Pageable pageable);


    @Query("""
        SELECT g
        FROM Guest g
        WHERE
            (
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
        """)
    Page<Guest> findGuestsWithFilters(
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("adultOrchild") String adultOrchild,
            @Param("guestCategory") String guestCategory,
            @Param("gift") String gift,
            @Param("stay") String stay,
            @Param("cash") String cash,
            Pageable pageable
    );

    @Query("""
    SELECT g
    FROM Guest g
    WHERE
        (
            :search = ''
            OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(g.id AS string) LIKE CONCAT('%', :search, '%')
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
    ORDER BY g.id ASC
""")
    List<Guest> findAllGuestsWithFilters(
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("adultOrchild") String adultOrchild,
            @Param("guestCategory") String guestCategory,
            @Param("gift") String gift,
            @Param("stay") String stay,
            @Param("cash") String cash
    );


}
