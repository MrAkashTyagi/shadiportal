package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.WeddingContactRequest;
import com.bigsquare.ShadiPortal.dto.WeddingContactResponse;


import com.bigsquare.ShadiPortal.entities.WeddingContact;
import com.bigsquare.ShadiPortal.repositories.WeddingContactRepository;
import com.bigsquare.ShadiPortal.services.WeddingContactService;
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

    private final WeddingContactRepository
            repository;

    @Override
    public WeddingContactResponse createContact(
            WeddingContactRequest request) {

        WeddingContact entity =
                WeddingContact.builder()
                        .name(request.getName())
                        .category(request.getCategory())
                        .contactPerson(request.getContactPerson())
                        .mobileNumber(request.getMobileNumber())
                        .alternateNumber(request.getAlternateNumber())
                        .whatsappNumber(request.getWhatsappNumber())
                        .address(request.getAddress())
                        .mapLink(request.getMapLink())
                        .notes(request.getNotes())
                        .advancePaid(request.getAdvancePaid())
                        .pendingAmount(request.getPendingAmount())
                        .build();

        return mapToResponse(
                repository.save(entity)
        );
    }

    @Override
    public WeddingContactResponse updateContact(
            Long id,
            WeddingContactRequest request) {

        WeddingContact entity =
                repository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Contact not found"
                                )
                        );

        entity.setName(
                request.getName()
        );

        entity.setCategory(
                request.getCategory()
        );

        entity.setContactPerson(
                request.getContactPerson()
        );

        entity.setMobileNumber(
                request.getMobileNumber()
        );

        entity.setAlternateNumber(
                request.getAlternateNumber()
        );

        entity.setWhatsappNumber(
                request.getWhatsappNumber()
        );

        entity.setAddress(
                request.getAddress()
        );

        entity.setMapLink(
                request.getMapLink()
        );

        entity.setNotes(
                request.getNotes()
        );

        entity.setAdvancePaid(
                request.getAdvancePaid()
        );

        entity.setPendingAmount(
                request.getPendingAmount()
        );

        return mapToResponse(
                repository.save(entity)
        );
    }

    @Override
    public WeddingContactResponse getContactById(
            Long id) {

        return mapToResponse(
                repository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Contact not found"
                                )
                        )
        );
    }

    @Override
    public List<WeddingContactResponse>
    getAllContacts() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteContact(Long id) {

        repository.deleteById(id);
    }

    private WeddingContactResponse mapToResponse(
            WeddingContact entity) {

        return WeddingContactResponse
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .contactPerson(entity.getContactPerson())
                .mobileNumber(entity.getMobileNumber())
                .alternateNumber(entity.getAlternateNumber())
                .whatsappNumber(entity.getWhatsappNumber())
                .address(entity.getAddress())
                .mapLink(entity.getMapLink())
                .notes(entity.getNotes())
                .advancePaid(entity.getAdvancePaid())
                .pendingAmount(entity.getPendingAmount())
                .build();
    }

    @Override
    public Page<WeddingContactResponse> getContacts(
            String search,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("name")
                                .ascending()
                );

        Page<WeddingContact> contacts =
                repository
                        .findByNameContainingIgnoreCase(
                                search == null
                                        ? ""
                                        : search,
                                pageable
                        );

        return contacts.map(
                this::mapToResponse
        );
    }
}
