package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.entities.Guest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


public interface GuestService {


    public Guest createGuest(Guest guest);

    public List<Guest> getAllGuests();

    public Guest getById(Integer id);

    public void delete(Integer id);

    public Guest updateGuest(Integer id, Guest guest);


    Page<Guest> getGuestWithPagination(int page,
                                       int size,
                                       String search,
                                       String gender,
                                       String adultOrchild,
                                       String cash,
                                       String gift,
                                       String stay,
                                       String category);

    ByteArrayInputStream getFilteredActualData(
            String search,
            String gender,
            String adultOrchild,
            String gift,
            String cash,
            String guestCategory,
            String stay
    ) throws IOException;
}
