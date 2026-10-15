package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.WeddingContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeddingContactRepository
        extends JpaRepository<WeddingContact, Long> {

    Optional<WeddingContact> findByIdAndUserId(
            Long id,
            Integer userId
    );

    Page<WeddingContact>
    findByUserIdAndNameContainingIgnoreCase(
            Integer userId,
            String search,
            Pageable pageable
    );

    List<WeddingContact> findAllByUserIdOrderByNameAsc(
            Integer userId
    );
}
