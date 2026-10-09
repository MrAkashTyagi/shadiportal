package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.WeddingContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeddingContactRepository
        extends JpaRepository<WeddingContact, Long> {

    Page<WeddingContact> findByNameContainingIgnoreCase(
            String search,
            Pageable pageable
    );
}
