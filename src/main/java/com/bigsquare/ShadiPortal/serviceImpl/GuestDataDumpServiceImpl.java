package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.helper.GuestHelper;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
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

    @Autowired
    private UserRepo userRepo;

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

    public ByteArrayInputStream getActualData(
            Integer userId
    ) throws IOException {

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        List<Guest> guestList =
                guestRepo.findByUserId(
                        userId
                );

        return GuestHelper.dataToExcel(
                guestList
        );
    }

    public void save(
            MultipartFile file,
            Integer userId
    ) {

        try {

            User user =
                    userRepo.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found"
                                    )
                            );

            List<Guest> guests =
                    GuestHelper.convertExcelToListOfGuests(
                            file.getInputStream()
                    );

            for (Guest guest : guests) {

                guest.setUser(user);

                if (
                        guest.getFamily() != null &&
                                guest.getFamily().getFamilyName() != null &&
                                !guest.getFamily()
                                        .getFamilyName()
                                        .isBlank()
                ) {

                    String familyName =
                            guest.getFamily()
                                    .getFamilyName()
                                    .trim();

                    Family family =
                            familyRepo
                                    .findByFamilyNameIgnoreCaseAndUserId(
                                            familyName,
                                            userId
                                    )
                                    .orElseGet(() -> {

                                        Family newFamily =
                                                new Family();

                                        newFamily.setFamilyName(
                                                familyName
                                        );

                                        newFamily.setUser(
                                                user
                                        );

                                        return familyRepo.save(
                                                newFamily
                                        );
                                    });

                    guest.setFamily(
                            family
                    );

                } else {

                    guest.setFamily(null);
                }
            }

            guestRepo.saveAll(
                    guests
            );

        } catch (Exception exception) {

            exception.printStackTrace();

            throw new RuntimeException(
                    "Error while importing guests",
                    exception
            );
        }
    }
}
