package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.Family;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FamilyRepo extends JpaRepository<Family, Integer> {
    Optional<Family> findByFamilyName(String familyName);

    public Optional<Family> findByFamilyNameIgnoreCase(String familyName);


    @Query("""
        SELECT f
        FROM Family f
        WHERE
            f.user.id = :userId
            AND
            (
                LOWER(f.familyName)
                LIKE LOWER(CONCAT('%', :search, '%'))

                OR

                CAST(f.id AS string)
                LIKE CONCAT('%', :search, '%')
            )
        """)
    Page<Family> findBySearchQuery(

            @Param("userId")
            Integer userId,

            @Param("search")
            String search,

            Pageable pageable
    );


    @Query("""
            SELECT COUNT(g)
            FROM Family f
            JOIN f.guestList g
            """)
    Long getTotalFamilyMembers();

    @Query("""
            SELECT COALESCE(MAX(SIZE(f.guestList)), 0)
            FROM Family f
            """)
    Integer getLargestFamilySize();

    Optional<Family> findByFamilyNameIgnoreCaseAndUserId(
            String familyName,
            Integer userId
    );

    Page<Family> findAllByUserId(
            Integer userId,
            Pageable pageable
    );

    List<Family> findAllByUserId(
            Integer userId
    );

    long countByUserId(
            Integer userId
    );

    @Query("""
       SELECT COUNT(g)
       FROM Family f
       JOIN f.guestList g
       WHERE f.user.id = :userId
       """)
    Long getTotalFamilyMembersByUserId(
            @Param("userId") Integer userId
    );

    List<Family> findByUserId(Long userId);


//    Page<Guest> findBySearchQuery(@Param("search") String search, Pageable pageable);
}
