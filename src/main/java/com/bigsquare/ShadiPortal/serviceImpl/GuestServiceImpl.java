package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.GiftSummaryDto;
import com.bigsquare.ShadiPortal.dto.GuestCategorySummaryDto;
import com.bigsquare.ShadiPortal.dto.GuestSummaryDto;
import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.helper.GuestHelper;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.GuestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    GuestRepo guestRepo;

    @Autowired
    private CurrentUserService currentUserService;

    @Override
    public Guest createGuest(
            Guest guest
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        User user = userRepo
                .findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        guest.setUser(user);

        if (
                guest.getFamily() != null &&
                        guest.getFamily().getFamilyName() != null &&
                        !guest.getFamily().getFamilyName().isBlank()
        ) {

            String inputFamilyName =
                    guest.getFamily()
                            .getFamilyName()
                            .trim();

            Optional<Family> existingFamily =
                    familyRepo
                            .findByFamilyNameIgnoreCaseAndUserId(
                                    inputFamilyName,
                                    userId
                            );

            if (existingFamily.isPresent()) {

                guest.setFamily(
                        existingFamily.get()
                );

            } else {

                Family newFamily = new Family();

                newFamily.setFamilyName(
                        inputFamilyName
                );

                newFamily.setUser(user);

                Family savedFamily =
                        familyRepo.save(newFamily);

                guest.setFamily(savedFamily);
            }

        } else {

            guest.setFamily(null);
        }

        return guestRepo.save(guest);
    }

    public List<Guest> getAllGuests() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return guestRepo.findAllByUserId(
                userId
        );
    }

    @Override
    public Guest getById(
            Integer id
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return guestRepo
                .findByIdAndUserId(
                        id,
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Guest not found"
                        )
                );
    }

    @Override
    public void delete(
            Integer id
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Guest guest =
                guestRepo
                        .findByIdAndUserId(
                                id,
                                userId
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Guest not found"
                                )
                        );

        guestRepo.delete(
                guest
        );
    }

    @Override
    public Guest updateGuest(
            Integer id,
            Guest guest
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Guest existingGuest =
                guestRepo
                        .findByIdAndUserId(
                                id,
                                userId
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Guest not found"
                                )
                        );

        existingGuest.setName(
                guest.getName()
        );

        existingGuest.setEmail(
                guest.getEmail()
        );

        existingGuest.setGuestCategory(
                guest.getGuestCategory()
        );

        existingGuest.setWhatsapp_Number(
                guest.getWhatsapp_Number()
        );

        existingGuest.setGender(
                guest.getGender()
        );

        existingGuest.setPhoneNumber(
                guest.getPhoneNumber()
        );

        existingGuest.setAdultOrchild(
                guest.getAdultOrchild()
        );

        existingGuest.setGift(
                guest.getGift()
        );

        existingGuest.setStay(
                guest.getStay()
        );

        existingGuest.setCash(
                guest.getCash()
        );

        existingGuest.setInvitationSent(
                guest.getInvitationSent()
        );

        if (guest.getFamily() == null) {

            existingGuest.setFamily(null);

        } else if (
                guest.getFamily().getId() != null
        ) {

            Family existingFamily =
                    familyRepo
                            .findByIdAndUserId(
                                    guest.getFamily().getId(),
                                    userId
                            )
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Family not found"
                                    )
                            );

            existingGuest.setFamily(
                    existingFamily
            );

        } else if (
                guest.getFamily()
                        .getFamilyName() != null &&
                        !guest.getFamily()
                                .getFamilyName()
                                .isBlank()
        ) {

            String familyName =
                    guest.getFamily()
                            .getFamilyName()
                            .trim();

            Family resolvedFamily =
                    familyRepo
                            .findByFamilyNameIgnoreCaseAndUserId(
                                    familyName,
                                    userId
                            )
                            .orElseGet(() -> {

                                Family newFamily =
                                        new Family();

                                newFamily.setFamilyName(
                                        familyName
                                );

                                newFamily.setUser(
                                        existingGuest.getUser()
                                );

                                return familyRepo.save(
                                        newFamily
                                );
                            });

            existingGuest.setFamily(
                    resolvedFamily
            );

        } else {

            existingGuest.setFamily(null);
        }

        return guestRepo.save(
                existingGuest
        );
    }

    @Override
    public Page<Guest> getGuestWithPagination(

            int page,

            int size,

            String search,

            String gender,

            String adultOrchild,

            String gift,

            String cash,

            String guestCategory,

            String stay,

            Boolean invitationSent
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id")
                                .ascending()
                );

        String searchValue = search == null ? "" : search.trim();

        String genderValue = gender == null ? "" : gender.trim();

        String typeValue = adultOrchild == null ? "" : adultOrchild.trim();

        String typegift = gift == null ? "" : gift.trim();

        String typestay = stay == null ? "" : stay.trim();

        String typecash = cash == null ? "" : cash.trim();

        String typeCategory = guestCategory == null ? "" : guestCategory.trim();

        // Agar search string null ya empty hai, toh normal sara data paged return karein
//        if (search == null || search.trim().isEmpty()) {
//            return this.guestRepo.findAll(pageable);
//        }

        // Agar search me kuch value hai, toh custom query chalayein
//        return this.guestRepo.findBySearchQuery(search.trim(), pageable);

        return this.guestRepo.findGuestsWithFilters(
                userId,
                searchValue,
                genderValue,
                typeValue,
                typeCategory,
                typegift,
                typestay,
                typecash,
                invitationSent,
                pageable
        );
    }

    public ByteArrayInputStream getActualData(
            Integer userId
    ) throws IOException {

        List<Guest> guestList =
                guestRepo.findAllByUserId(
                        userId
                );

        return GuestHelper.dataToExcel(
                guestList
        );
    }

    @Override
    public ByteArrayInputStream getFilteredActualData(
            String search,
            String gender,
            String adultOrchild,
            String gift,
            String cash,
            String guestCategory,
            String stay,
            Boolean invitationSent
    )
            throws IOException {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        String searchValue =
                search == null
                        ? ""
                        : search.trim();

        String genderValue =
                gender == null
                        ? ""
                        : gender.trim();

        String typeValue =
                adultOrchild == null
                        ? ""
                        : adultOrchild.trim();

        String giftValue =
                gift == null
                        ? ""
                        : gift.trim();

        String cashValue =
                cash == null
                        ? ""
                        : cash.trim();

        String categoryValue =
                guestCategory == null
                        ? ""
                        : guestCategory.trim();

        String stayValue =
                stay == null
                        ? ""
                        : stay.trim();

        List<Guest> guestList =
                guestRepo.findAllGuestsWithFilters(
                        userId,
                        searchValue,
                        genderValue,
                        typeValue,
                        categoryValue,
                        giftValue,
                        stayValue,
                        cashValue,
                        invitationSent
                );

        return GuestHelper.dataToExcel(
                guestList
        );
    }

    @Override
    public GuestSummaryDto getGuestSummary(

    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return new GuestSummaryDto(

                guestRepo.countByUserId(
                        userId
                ),

                guestRepo.countByUserIdAndInvitationSentTrue(
                        userId
                ),

                guestRepo.countPendingInvitationsByUserId(
                        userId
                ),

                guestRepo.countByUserIdAndStay(
                        userId,
                        "Yes"
                )

        );
    }

    @Override
    public List<GuestCategorySummaryDto>
    getGuestCategorySummary() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return guestRepo
                .getGuestCategorySummary(
                        userId
                );
    }


    public List<Guest> getGuestsByUserId(
            Integer userId
    ) {

        return guestRepo.findByUserId(
                userId
        );

    }

    @Override
    public List<GiftSummaryDto> getGiftSummary(

    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        List<Guest> guests =
                guestRepo.findAllByUserId(
                        userId
                );

        Map<String, Long> giftCounts =
                new TreeMap<>(
                        String.CASE_INSENSITIVE_ORDER
                );

        for (Guest guest : guests) {

            String giftValue =
                    guest.getGift();

            if (
                    giftValue == null ||
                            giftValue.isBlank()
            ) {
                continue;
            }

            String[] gifts =
                    giftValue.split(",");

            for (String gift : gifts) {

                String normalizedGift =
                        gift.trim();

                if (normalizedGift.isBlank()) {
                    continue;
                }

                giftCounts.merge(
                        normalizedGift,
                        1L,
                        Long::sum
                );
            }
        }

        return giftCounts
                .entrySet()
                .stream()
                .map(entry ->
                        new GiftSummaryDto(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }

}
