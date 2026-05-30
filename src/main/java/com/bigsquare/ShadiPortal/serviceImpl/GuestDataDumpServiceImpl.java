package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.helper.GuestHelper;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class GuestDataDumpServiceImpl {

    @Autowired
    private GuestRepo guestRepo;

    @Autowired
    private FamilyRepo familyRepo;

    //saving data from excel to db
//    public void save(MultipartFile file) {
//
//        try {
//            List<Guest> guests = GuestHelper.convertExcelToListOfGuests(file.getInputStream());
//            this.guestRepo.saveAll(guests);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    public List<Guest> getAllGuests() {
        return this.guestRepo.findAll();
    }

    public ByteArrayInputStream getActualData() throws IOException {
        List<Guest> guestList = this.guestRepo.findAll();
        System.out.println(guestList);
        ByteArrayInputStream stream = GuestHelper.dataToExcel(guestList);
        return stream;
    }

    public void save(MultipartFile file) {
        try {
            List<Guest> guests = GuestHelper.convertExcelToListOfGuests(file.getInputStream());

            // Loop chala kar har guest ki Family ko database se link karein
            for (Guest guest : guests) {
                if (guest.getFamily() != null && guest.getFamily().getId() != null) {
                    Integer famId = guest.getFamily().getId();

                    // Database se real, managed Family record uthayein
                    Family managedFamily = familyRepo.findById(famId).orElse(null);

                    // Agar database mein family mili toh wahi set karein, nahi toh null
                    guest.setFamily(managedFamily);
                }
            }

            this.guestRepo.saveAll(guests);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
