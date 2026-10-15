package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.WeddingContact;
import com.bigsquare.ShadiPortal.helper.WeddingContactHelper;
import com.bigsquare.ShadiPortal.repositories.WeddingContactRepository;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeddingContactDataDumpServiceImpl {

    private final WeddingContactRepository repository;

    private final CurrentUserService currentUserService;

    @Transactional
    public int save(
            MultipartFile file
    ) {

        if (
                file == null
                        || file.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Excel file is required"
            );
        }

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        try {

            List<WeddingContact> contacts =
                    WeddingContactHelper
                            .convertExcelToListOfContacts(
                                    file.getInputStream()
                            );

            if (contacts.isEmpty()) {

                throw new IllegalArgumentException(
                        "Excel file does not contain any contact data"
                );
            }

            for (WeddingContact contact : contacts) {

                contact.setId(null);

                /*
                 * Excel/frontend se userId accept nahi hoga.
                 * Authenticated wedding owner hi assign hoga.
                 */
                contact.setUserId(
                        ownerUserId
                );
            }

            List<WeddingContact> savedContacts =
                    repository.saveAll(
                            contacts
                    );

            return savedContacts.size();

        } catch (IllegalArgumentException exception) {

            throw exception;

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Unable to read wedding contacts Excel file",
                    exception
            );

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Error importing wedding contacts",
                    exception
            );
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream getActualData()
            throws IOException {

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        List<WeddingContact> contacts =
                repository
                        .findAllByUserIdOrderByNameAsc(
                                ownerUserId
                        );

        return WeddingContactHelper
                .dataToExcel(
                        contacts
                );
    }
}
