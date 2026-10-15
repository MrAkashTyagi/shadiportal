package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.WeddingContactRequest;
import com.bigsquare.ShadiPortal.dto.WeddingContactResponse;
import com.bigsquare.ShadiPortal.entities.WeddingContact;
import com.bigsquare.ShadiPortal.repositories.WeddingContactRepository;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.WeddingContactService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeddingContactServiceImpl
        implements WeddingContactService {

    private final WeddingContactRepository repository;

    private final CurrentUserService currentUserService;

    @Override
    public WeddingContactResponse createContact(
            WeddingContactRequest request
    ) {

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        WeddingContact entity =
                WeddingContact.builder()
                        .userId(ownerUserId)
                        .name(normalizeRequiredText(
                                request.getName(),
                                "Contact name is required"
                        ))
                        .category(normalizeText(
                                request.getCategory()
                        ))
                        .contactPerson(normalizeText(
                                request.getContactPerson()
                        ))
                        .mobileNumber(normalizeText(
                                request.getMobileNumber()
                        ))
                        .alternateNumber(normalizeText(
                                request.getAlternateNumber()
                        ))
                        .whatsappNumber(normalizeText(
                                request.getWhatsappNumber()
                        ))
                        .address(normalizeText(
                                request.getAddress()
                        ))
                        .mapLink(normalizeText(
                                request.getMapLink()
                        ))
                        .notes(normalizeText(
                                request.getNotes()
                        ))
                        .advancePaid(normalizeAmount(
                                request.getAdvancePaid()
                        ))
                        .pendingAmount(normalizeAmount(
                                request.getPendingAmount()
                        ))
                        .build();

        validateAmounts(entity);

        WeddingContact savedContact =
                repository.save(entity);

        return mapToResponse(savedContact);
    }

    @Override
    public WeddingContactResponse updateContact(
            Long id,
            WeddingContactRequest request
    ) {

        WeddingContact entity =
                getOwnedContact(id);

        entity.setName(
                normalizeRequiredText(
                        request.getName(),
                        "Contact name is required"
                )
        );

        entity.setCategory(
                normalizeText(
                        request.getCategory()
                )
        );

        entity.setContactPerson(
                normalizeText(
                        request.getContactPerson()
                )
        );

        entity.setMobileNumber(
                normalizeText(
                        request.getMobileNumber()
                )
        );

        entity.setAlternateNumber(
                normalizeText(
                        request.getAlternateNumber()
                )
        );

        entity.setWhatsappNumber(
                normalizeText(
                        request.getWhatsappNumber()
                )
        );

        entity.setAddress(
                normalizeText(
                        request.getAddress()
                )
        );

        entity.setMapLink(
                normalizeText(
                        request.getMapLink()
                )
        );

        entity.setNotes(
                normalizeText(
                        request.getNotes()
                )
        );

        entity.setAdvancePaid(
                normalizeAmount(
                        request.getAdvancePaid()
                )
        );

        entity.setPendingAmount(
                normalizeAmount(
                        request.getPendingAmount()
                )
        );

        /*
         * userId ko request se kabhi update nahi karna.
         * Existing ownership same rehni chahiye.
         */
        validateAmounts(entity);

        WeddingContact updatedContact =
                repository.save(entity);

        return mapToResponse(updatedContact);
    }

    @Override
    public WeddingContactResponse getContactById(
            Long id
    ) {

        return mapToResponse(
                getOwnedContact(id)
        );
    }

    @Override
    public List<WeddingContactResponse>
    getAllContacts() {

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        return repository
                .findAllByUserIdOrderByNameAsc(
                        ownerUserId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteContact(
            Long id
    ) {

        WeddingContact contact =
                getOwnedContact(id);

        repository.delete(contact);
    }

    @Override
    public Page<WeddingContactResponse> getContacts(
            String search,
            int page,
            int size
    ) {

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        validatePagination(
                page,
                size
        );

        String searchValue =
                search == null
                        ? ""
                        : search.trim();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("name")
                                .ascending()
                );

        Page<WeddingContact> contacts =
                repository
                        .findByUserIdAndNameContainingIgnoreCase(
                                ownerUserId,
                                searchValue,
                                pageable
                        );

        return contacts.map(
                this::mapToResponse
        );
    }

    private WeddingContact getOwnedContact(
            Long contactId
    ) {

        if (contactId == null) {

            throw new IllegalArgumentException(
                    "Contact id is required"
            );
        }

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        return repository
                .findByIdAndUserId(
                        contactId,
                        ownerUserId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Contact not found with id: "
                                        + contactId
                        )
                );
    }

    private WeddingContactResponse mapToResponse(
            WeddingContact entity
    ) {

        return WeddingContactResponse
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .contactPerson(
                        entity.getContactPerson()
                )
                .mobileNumber(
                        entity.getMobileNumber()
                )
                .alternateNumber(
                        entity.getAlternateNumber()
                )
                .whatsappNumber(
                        entity.getWhatsappNumber()
                )
                .address(entity.getAddress())
                .mapLink(entity.getMapLink())
                .notes(entity.getNotes())
                .advancePaid(
                        entity.getAdvancePaid()
                )
                .pendingAmount(
                        entity.getPendingAmount()
                )
                .build();
    }

    private String normalizeRequiredText(
            String value,
            String errorMessage
    ) {

        String normalizedValue =
                normalizeText(value);

        if (normalizedValue == null) {

            throw new IllegalArgumentException(
                    errorMessage
            );
        }

        return normalizedValue;
    }

    private String normalizeText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalizedValue =
                value.trim();

        return normalizedValue.isEmpty()
                ? null
                : normalizedValue;
    }

    private Double normalizeAmount(
            Double amount
    ) {

        return amount == null
                ? 0.0
                : amount;
    }

    private void validateAmounts(
            WeddingContact contact
    ) {

        if (
                contact.getAdvancePaid() != null
                        && contact.getAdvancePaid() < 0
        ) {

            throw new IllegalArgumentException(
                    "Advance paid cannot be negative"
            );
        }

        if (
                contact.getPendingAmount() != null
                        && contact.getPendingAmount() < 0
        ) {

            throw new IllegalArgumentException(
                    "Pending amount cannot be negative"
            );
        }
    }

    private void validatePagination(
            int page,
            int size
    ) {

        if (page < 0) {

            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size <= 0 || size > 100) {

            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }
    }
}
