package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FamilyRepo extends JpaRepository<Family,Integer> {
    Optional<Family> findByFamilyName(String familyName);

    public Optional<Family> findByFamilyNameIgnoreCase(String familyName);

}
