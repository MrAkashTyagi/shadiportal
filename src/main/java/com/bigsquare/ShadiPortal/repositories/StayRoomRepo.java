package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.StayRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StayRoomRepo
        extends JpaRepository<StayRoom, Integer> {

    List<StayRoom>
    findAllByLocationIdAndLocationUserIdOrderByNameAsc(
            Integer locationId,
            Integer userId
    );

    List<StayRoom>
    findAllByLocationIdAndLocationUserIdAndActiveTrueOrderByNameAsc(
            Integer locationId,
            Integer userId
    );

    List<StayRoom>
    findAllByLocationUserIdOrderByLocationNameAscNameAsc(
            Integer userId
    );

    Optional<StayRoom>
    findByIdAndLocationUserId(
            Integer roomId,
            Integer userId
    );

    boolean existsByLocationIdAndNameIgnoreCase(
            Integer locationId,
            String name
    );

    boolean existsByLocationIdAndNameIgnoreCaseAndIdNot(
            Integer locationId,
            String name,
            Integer roomId
    );

    long countByLocationIdAndLocationUserId(
            Integer locationId,
            Integer userId
    );
}
