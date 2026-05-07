package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestServiceImpl {

    @Autowired
    GuestRepo guestRepo;

    public Guest saveGuest(){

        return new Guest();
    }

    public List<Guest> getAllGuests(){

        List<Guest> guests = (List<Guest>) guestRepo.findAll();
        return guests;
    }

}
