package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.entities.Family;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FamilyService {

    public Family createFamily(Family family);

    public List<Family> getAllFamilies();

    public Family updateFamily(Integer id, Family family);

    public Family getByFamilityId(Integer id);

    public void deleteFamily(Integer id);

    Page<Family> getPaginatedFamilyResult(int page, int size, String search);

}
