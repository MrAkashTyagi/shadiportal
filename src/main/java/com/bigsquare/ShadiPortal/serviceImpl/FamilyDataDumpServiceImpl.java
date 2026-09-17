package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.helper.FamilyHelper;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
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

    @Autowired
    private CurrentUserService currentUserService;

    public void save(
            MultipartFile file
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

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
                                .findFirstByFamilyNameIgnoreCaseAndUserIdOrderByIdAsc(
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


    public List<Family> getAllFamilies(
    ) {
        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return familyRepo.findAllByUserId(
                userId
        );
    }

    public ByteArrayInputStream getActualData(

    ) throws IOException {

        Integer userId =
                currentUserService
                        .getCurrentUserId();


        List<Family> familyList =
                familyRepo.findAllByUserId(
                        userId
                );

        return FamilyHelper.dataToExcel(
                familyList
        );
    }

}
