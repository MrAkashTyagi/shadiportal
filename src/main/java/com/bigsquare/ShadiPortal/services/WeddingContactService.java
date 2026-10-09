package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.WeddingContactRequest;
import com.bigsquare.ShadiPortal.dto.WeddingContactResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WeddingContactService {

    WeddingContactResponse createContact(
            WeddingContactRequest request);

    WeddingContactResponse updateContact(
            Long id,
            WeddingContactRequest request);

    WeddingContactResponse getContactById(
            Long id);

    List<WeddingContactResponse> getAllContacts();

    void deleteContact(Long id);

    Page<WeddingContactResponse> getContacts(
            String search,
            int page,
            int size
    );
}
