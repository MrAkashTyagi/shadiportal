package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.FamilyRequest;
import com.bigsquare.ShadiPortal.dto.FamilySummaryDto;
import com.bigsquare.ShadiPortal.entities.Family;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FamilyService {

    public Family createFamily(FamilyRequest request);

//    public List<Family> getAllFamilies();

    List<Family> getAllFamilies(Long userId);

    public Family updateFamily(Integer id, FamilyRequest request);

    public Family getByFamilityId(Integer id);

    public void deleteFamily(Integer id);

    Page<Family> getPaginatedFamilyResult(Integer userId, int page, int size, String search);

    FamilySummaryDto getFamilySummary();



}
