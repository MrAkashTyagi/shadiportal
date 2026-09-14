package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.helper.FamilyHelper;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FamilyDataDumpServiceImpl {


    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private UserRepo userRepo;

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

            List<Family> families =
                    FamilyHelper.convertExcelToListOfFamilies(
                            file.getInputStream()
                    );
            List<Family> familiesToSave =
                    new ArrayList<>();

            for (Family family : families) {

                String familyName =
                        family.getFamilyName()
                                .trim()
                                .replaceAll("\\s+", " ");

                boolean exists =
                        familyRepo
                                .findByFamilyNameIgnoreCaseAndUserId(
                                        familyName,
                                        userId
                                )
                                .isPresent();

                if (!exists) {

                    family.setId(null);

                    family.setFamilyName(
                            familyName
                    );

                    family.setUser(user);

                    familiesToSave.add(
                            family
                    );
                }
            }

            this.familyRepo.saveAll(
                    familiesToSave
            );

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

//    public List<Family> getAllGuests(){
//        return this.familyRepo.findAllByUserId(
//                userId
//        );
//    }

    public List<Family> getAllFamilies(
            Integer userId
    ) {

        return familyRepo.findAllByUserId(
                userId
        );
    }

    public ByteArrayInputStream getActualData(
            Integer userId
    ) throws IOException {

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        List<Family> familyList =
                familyRepo.findAllByUserId(
                        userId
                );

        return FamilyHelper.dataToExcel(
                familyList
        );
    }

}
