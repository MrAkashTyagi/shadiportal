package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.WallMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WallMediaRepo
        extends JpaRepository<WallMedia, Integer> {

    List<WallMedia>
    findAllByOwnerUserIdOrderByUploadedAtDesc(
            Integer ownerUserId
    );

    Optional<WallMedia>
    findByIdAndOwnerUserId(
            Integer mediaId,
            Integer ownerUserId
    );
}
