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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class GuestServiceImpl implements GuestService {

    private final FamilyRepo familyRepo;
    private final UserRepo userRepo;
    private final GuestRepo guestRepo;
    private final CurrentUserService currentUserService;

    public GuestServiceImpl(
            FamilyRepo familyRepo,
            UserRepo userRepo,
            GuestRepo guestRepo,
            CurrentUserService currentUserService
    ) {
        this.familyRepo = familyRepo;
        this.userRepo = userRepo;
        this.guestRepo = guestRepo;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public Guest createGuest(Guest guest) {
        Integer userId = getCurrentUserId();
        User user = getUserById(userId);

        guest.setUser(user);
        guest.setFamily(resolveFamily(guest.getFamily(), user));

        return guestRepo.save(guest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guest> getAllGuests() {
        Integer userId = getCurrentUserId();

        return guestRepo.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Guest getById(Integer id) {
        Integer userId = getCurrentUserId();

        return getGuestByIdAndUserId(id, userId);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Integer userId = getCurrentUserId();
        Guest guest = getGuestByIdAndUserId(id, userId);

        guestRepo.delete(guest);
    }

    @Override
    @Transactional
    public Guest updateGuest(Integer id, Guest guest) {
        Integer userId = getCurrentUserId();
        Guest existingGuest = getGuestByIdAndUserId(id, userId);

        updateGuestFields(existingGuest, guest);

        Family resolvedFamily = resolveFamily(
                guest.getFamily(),
                existingGuest.getUser()
        );

        existingGuest.setFamily(resolvedFamily);

        return guestRepo.save(existingGuest);
    }

    @Override
    @Transactional(readOnly = true)
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
        Integer userId = getCurrentUserId();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );

        return guestRepo.findGuestsWithFilters(
                userId,
                normalize(search),
                normalize(gender),
                normalize(adultOrchild),
                normalize(guestCategory),
                normalize(gift),
                normalize(stay),
                normalize(cash),
                invitationSent,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream getActualData(Integer userId)
            throws IOException {

        List<Guest> guestList = guestRepo.findAllByUserId(userId);

        return GuestHelper.dataToExcel(guestList);
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream getFilteredActualData(
            String search,
            String gender,
            String adultOrchild,
            String gift,
            String cash,
            String guestCategory,
            String stay,
            Boolean invitationSent
    ) throws IOException {

        Integer userId = getCurrentUserId();

        List<Guest> guestList = guestRepo.findAllGuestsWithFilters(
                userId,
                normalize(search),
                normalize(gender),
                normalize(adultOrchild),
                normalize(guestCategory),
                normalize(gift),
                normalize(stay),
                normalize(cash),
                invitationSent
        );

        return GuestHelper.dataToExcel(guestList);
    }

    @Override
    @Transactional(readOnly = true)
    public GuestSummaryDto getGuestSummary() {
        Integer userId = getCurrentUserId();

        return new GuestSummaryDto(
                guestRepo.countByUserId(userId),
                guestRepo.countByUserIdAndInvitationSentTrue(userId),
                guestRepo.countPendingInvitationsByUserId(userId),
                guestRepo.countByUserIdAndStay(userId, "Yes")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuestCategorySummaryDto> getGuestCategorySummary() {
        Integer userId = getCurrentUserId();

        return guestRepo.getGuestCategorySummary(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guest> getGuestsByUserId(Integer userId) {
        return guestRepo.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftSummaryDto> getGiftSummary() {
        Integer userId = getCurrentUserId();
        List<Guest> guests = guestRepo.findAllByUserId(userId);

        Map<String, Long> giftCounts = new TreeMap<>(
                String.CASE_INSENSITIVE_ORDER
        );

        for (Guest guest : guests) {
            String giftValue = guest.getGift();

            if (giftValue == null || giftValue.isBlank()) {
                continue;
            }

            String[] gifts = giftValue.split(",");

            for (String gift : gifts) {
                String normalizedGift = gift.trim();

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

        return giftCounts.entrySet()
                .stream()
                .map(entry -> new GiftSummaryDto(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

    private Integer getCurrentUserId() {
        Integer userId = currentUserService.getCurrentUserId();

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        return userId;
    }

    private User getUserById(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + userId
                ));
    }

    private Guest getGuestByIdAndUserId(
            Integer guestId,
            Integer userId
    ) {
        return guestRepo.findByIdAndUserId(guestId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Guest not found with id: " + guestId
                ));
    }

    private Family resolveFamily(
            Family requestedFamily,
            User user
    ) {
        if (requestedFamily == null) {
            return null;
        }

        Integer userId = user.getId();

        if (requestedFamily.getId() != null) {
            return familyRepo.findByIdAndUserId(
                            requestedFamily.getId(),
                            userId
                    )
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Family not found with id: "
                                    + requestedFamily.getId()
                    ));
        }

        String familyName = normalize(
                requestedFamily.getFamilyName()
        );

        if (familyName.isEmpty()) {
            return null;
        }

        return familyRepo
                .findByFamilyNameIgnoreCaseAndUserId(
                        familyName,
                        userId
                )
                .orElseGet(() -> {
                    Family newFamily = new Family();
                    newFamily.setFamilyName(familyName);
                    newFamily.setUser(user);

                    return familyRepo.save(newFamily);
                });
    }

    private void updateGuestFields(
            Guest existingGuest,
            Guest requestedGuest
    ) {
        existingGuest.setName(
                requestedGuest.getName()
        );

        existingGuest.setEmail(
                requestedGuest.getEmail()
        );

        existingGuest.setGuestCategory(
                requestedGuest.getGuestCategory()
        );

        existingGuest.setWhatsapp_Number(
                requestedGuest.getWhatsapp_Number()
        );

        existingGuest.setGender(
                requestedGuest.getGender()
        );

        existingGuest.setPhoneNumber(
                requestedGuest.getPhoneNumber()
        );

        existingGuest.setAdultOrchild(
                requestedGuest.getAdultOrchild()
        );

        existingGuest.setGift(
                requestedGuest.getGift()
        );

        existingGuest.setStay(
                requestedGuest.getStay()
        );

        existingGuest.setCash(
                requestedGuest.getCash()
        );

        existingGuest.setInvitationSent(
                requestedGuest.getInvitationSent()
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
