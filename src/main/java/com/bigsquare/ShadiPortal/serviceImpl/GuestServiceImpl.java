package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.services.GuestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    GuestRepo guestRepo;

    public Guest saveGuest(Guest guest) {

        if (guest.getFamily() != null && guest.getFamily().getFamilyName() != null) {

            String inputFamilyName = guest.getFamily().getFamilyName().trim();

            Optional<Family> existingFamily = familyRepo.findByFamilyNameIgnoreCase(inputFamilyName);
            if (existingFamily.isPresent()) {
                guest.setFamily(existingFamily.get());
            } else {
                guest.getFamily().setFamilyName(inputFamilyName);
            }

            // Family existingFamily = familyRepo.findById(guest.getFamily().getId()).orElseThrow(() -> new EntityNotFoundException("Family not found"));
            //guest.setFamily(existingFamily);

        }

        return this.guestRepo.save(guest);
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = guestRepo.findAll();
        return guests;
    }

    @Override
    public Guest getById(Integer id) {

        Guest guest = this.guestRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Guest With given id is not found in the database !!"));
        return guest;

    }

    @Override
    public void delete(Integer id) {
        Guest guest = this.guestRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Guest does not exists !!"));
        this.guestRepo.delete(guest);
    }

    @Override
    public Guest updateGuest(Integer id, Guest guest) {

//        get user

        Guest existingGuest = this.guestRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found !!"));

        existingGuest.setName(guest.getName());
        existingGuest.setEmail(guest.getEmail());
        existingGuest.setGuestCategory(guest.getGuestCategory());
        existingGuest.setWhatsapp_Number(guest.getWhatsapp_Number());
        existingGuest.setGender(guest.getGender());
        existingGuest.setFamily(guest.getFamily());
        existingGuest.setPhoneNumber(guest.getPhoneNumber());
        existingGuest.setAdultOrchild(guest.getAdultOrchild());

        // 2. SAFE FAMILY LOGIC (No Null ID Crash)
        if (guest.getFamily() != null) {

            // AGAR USER NE DROPDOWN YA KAHI SE EXISTNG FAMILY KI ID BHEJI HAI
            if (guest.getFamily().getId() != null) {
                Family existingFamily = this.familyRepo.findById(guest.getFamily().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Family not found !!"));
                existingGuest.setFamily(existingFamily);
            }

            // AGAR ID NAHI HAI PAR FAMILY NAME BHEJA HAI (Aapke Angular Form Jaisa Case)
            else if (guest.getFamily().getFamilyName() != null && !guest.getFamily().getFamilyName().trim().isEmpty()) {
                String inputFamilyName = guest.getFamily().getFamilyName().trim();

                // Database me same name ka parivar dhoondenge (Unique and Duplicate check)
                Optional<Family> dbFamily = this.familyRepo.findByFamilyName(inputFamilyName);

                if (dbFamily.isPresent()) {
                    existingGuest.setFamily(dbFamily.get()); // Purane se link kar do
                } else {
                    Family newFamily = new Family();
                    newFamily.setFamilyName(inputFamilyName);
                    existingGuest.setFamily(newFamily); // Naya parivar bna do
                }
            }
        } else {
            existingGuest.setFamily(null);
        }

        return this.guestRepo.save(existingGuest);
    }

    @Override
    public Page<Guest> getGuestWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return this.guestRepo.findAll(pageable);
    }

    @Override
    public Guest createGuest(Guest guest) {
        return this.guestRepo.save(guest);
    }
}
