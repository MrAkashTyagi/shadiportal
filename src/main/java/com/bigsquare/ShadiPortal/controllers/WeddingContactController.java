package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.WeddingContactRequest;
import com.bigsquare.ShadiPortal.dto.WeddingContactResponse;
import com.bigsquare.ShadiPortal.services.WeddingContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wedding-contacts")
@RequiredArgsConstructor
public class WeddingContactController {

    private final WeddingContactService service;

    @PostMapping
    public WeddingContactResponse create(
            @RequestBody WeddingContactRequest request
    ) {

        return service.createContact(request);
    }

    @PutMapping("/{id}")
    public WeddingContactResponse update(
            @PathVariable Long id,
            @RequestBody WeddingContactRequest request
    ) {

        return service.updateContact(
                id,
                request
        );
    }

    @GetMapping("/{id}")
    public WeddingContactResponse getById(
            @PathVariable Long id
    ) {

        return service.getContactById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        service.deleteContact(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping
    public Page<WeddingContactResponse> getContacts(
            @RequestParam(
                    defaultValue = ""
            )
            String search,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size
    ) {

        return service.getContacts(
                search,
                page,
                size
        );
    }
}
