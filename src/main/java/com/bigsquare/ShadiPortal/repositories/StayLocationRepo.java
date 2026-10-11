package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.StayLocation;
import com.bigsquare.ShadiPortal.enums.StayLocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StayLocationRepo
        extends JpaRepository<StayLocation, Integer> {

    List<StayLocation>
    findAllByUserIdOrderByNameAsc(
            Integer userId
    );

    List<StayLocation>
    findAllByUserIdAndActiveTrueOrderByNameAsc(
            Integer userId
    );

    List<StayLocation>
    findAllByUserIdAndTypeOrderByNameAsc(
            Integer userId,
            StayLocationType type
    );

    Optional<StayLocation>
    findByIdAndUserId(
            Integer locationId,
            Integer userId
    );

    boolean existsByUserIdAndNameIgnoreCase(
            Integer userId,
            String name
    );

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(
            Integer userId,
            String name,
            Integer locationId
    );
}
