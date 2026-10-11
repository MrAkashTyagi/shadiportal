package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.StayAllocation;
import com.bigsquare.ShadiPortal.enums.StayAllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StayAllocationRepo
        extends JpaRepository<StayAllocation, Integer> {

    Optional<StayAllocation> findByIdAndUserId(
            Integer allocationId,
            Integer userId
    );

    List<StayAllocation> findAllByUserIdOrderByIdDesc(
            Integer userId
    );

    List<StayAllocation> findAllByRoomIdAndUserIdOrderByIdAsc(
            Integer roomId,
            Integer userId
    );

    List<StayAllocation> findAllByGuestIdAndUserIdOrderByIdDesc(
            Integer guestId,
            Integer userId
    );

    Optional<StayAllocation>
    findFirstByGuestIdAndUserIdAndStatusInOrderByIdDesc(
            Integer guestId,
            Integer userId,
            Collection<StayAllocationStatus> statuses
    );

    long countByRoomIdAndUserIdAndStatusIn(
            Integer roomId,
            Integer userId,
            Collection<StayAllocationStatus> statuses
    );

    @Query("""
            SELECT allocation
            FROM StayAllocation allocation
            WHERE allocation.user.id = :userId
              AND (
                    :status IS NULL
                    OR allocation.status = :status
              )
              AND (
                    :roomId IS NULL
                    OR allocation.room.id = :roomId
              )
              AND (
                    :locationId IS NULL
                    OR allocation.room.location.id = :locationId
              )
            ORDER BY allocation.id DESC
            """)
    List<StayAllocation> findAllocationsWithFilters(
            @Param("userId")
            Integer userId,

            @Param("locationId")
            Integer locationId,

            @Param("roomId")
            Integer roomId,

            @Param("status")
            StayAllocationStatus status
    );
}
