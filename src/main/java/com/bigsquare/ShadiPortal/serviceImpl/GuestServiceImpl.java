package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.services.GuestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    GuestRepo guestRepo;

    public Guest saveGuest( Guest guest){

        if (guest.getFamily() != null && guest.getFamily().getId() != null){

            String inputFamilyName = guest.getFamily().getFamilyName();

            Optional<Family> existingFamily = familyRepo.findByFamilyName(inputFamilyName);
            if (existingFamily.isPresent()){
                guest.setFamily(existingFamily.get());
            }else {
                guest.getFamily().setId(null);
            }

           // Family existingFamily = familyRepo.findById(guest.getFamily().getId()).orElseThrow(() -> new EntityNotFoundException("Family not found"));
            //guest.setFamily(existingFamily);

        }

        return this.guestRepo.save(guest);
    }

    public List<Guest> getAllGuests(){
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
        return null;
    }

    @Override
    public Guest createGuest(Guest guest) {
        return this.guestRepo.save(guest);
    }
}
