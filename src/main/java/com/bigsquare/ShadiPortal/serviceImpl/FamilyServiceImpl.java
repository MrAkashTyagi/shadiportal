package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.FamilyRequest;
import com.bigsquare.ShadiPortal.dto.FamilySummaryDto;
import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.FamilyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GuestRepo guestRepo;

    @Autowired
    private CurrentUserService currentUserService;

    @Override
    public Family createFamily(
            FamilyRequest request
    ) {

        if (
                request.getFamilyName() == null ||
                        request.getFamilyName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Family name is required"
            );
        }


        String familyName =
                request.getFamilyName().trim();

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        User user = userRepo
                .findById(
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found with id: "
                                        + request.getUserId()
                        )
                );

        Optional<Family> existingFamily =
                familyRepo
                        .findByFamilyNameIgnoreCaseAndUserId(
                                familyName,
                                user.getId()
                        );

        if (existingFamily.isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Family already exists"
            );
        }

        Family family = new Family();

        family.setFamilyName(familyName);
        family.setUser(user);

        return familyRepo.save(family);
    }


    @Override
    public List<Family> getAllFamilies() {
        Integer userId =
                currentUserService
                        .getCurrentUserId();
        return familyRepo.findByUserId(
                userId.longValue()
        );
    }

    @Override
    public Family updateFamily(
            Integer id,
            FamilyRequest request
    ) {

        if (
                request.getFamilyName() == null ||
                        request.getFamilyName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Family name is required"
            );
        }

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Family existingFamily =
                familyRepo
                        .findByIdAndUserId(
                                id,
                                userId
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Family not found"
                                )
                        );

        String familyName =
                request.getFamilyName()
                        .trim();

        Optional<Family> duplicateFamily =
                familyRepo
                        .findByFamilyNameIgnoreCaseAndUserId(
                                familyName,
                                userId
                        );

        if (
                duplicateFamily.isPresent() &&
                        !duplicateFamily
                                .get()
                                .getId()
                                .equals(id)
        ) {
            throw new IllegalArgumentException(
                    "Family with this name already exists"
            );
        }

        existingFamily.setFamilyName(
                familyName
        );

        return familyRepo.save(
                existingFamily
        );
    }

    @Override
    public Family getByFamilityId(
            Integer id
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return familyRepo
                .findByIdAndUserId(
                        id,
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Family not found"
                        )
                );
    }

    @Override
    public void deleteFamily(
            Integer familyId
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Family family =
                familyRepo
                        .findByIdAndUserId(
                                familyId,
                                userId
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Family not found"
                                )
                        );

        long guestCount =
                guestRepo
                        .countByFamilyIdAndUserId(
                                familyId,
                                userId
                        );

        if (guestCount > 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This family contains "
                            + guestCount
                            + " guest(s). Delete the guests first."
            );
        }

        familyRepo.delete(
                family
        );
    }

    @Override
    public Page<Family> getPaginatedFamilyResult(int page, int size, String search) {


        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        String searchValue = search == null ? "" : search.trim();
        if (searchValue.isEmpty()) {

            return this.familyRepo.findAllByUserId(
                    userId,
                    pageable
            );

        }
        // Agar search me kuch value hai, toh custom query chalayein
        return this.familyRepo.findBySearchQuery(
                userId,
                searchValue,
                pageable);

    }

    @Override
    public FamilySummaryDto getFamilySummary() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Long totalFamilies =
                familyRepo.countByUserId(
                        userId
                );

        Long totalFamilyMembers =
                familyRepo
                        .getTotalFamilyMembersByUserId(
                                userId
                        );

        Integer largestFamilySize =
                familyRepo
                        .getLargestFamilySizeByUserId(
                                userId
                        );

        return new FamilySummaryDto(
                totalFamilies,
                totalFamilyMembers,
                largestFamilySize
        );
    }
}


