package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.WeddingSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeddingSettingsRepo
        extends JpaRepository<
        WeddingSettings,
        Integer
        > {

    Optional<WeddingSettings>
    findByUserId(
            Integer userId
    );
}
