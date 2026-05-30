package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.helper.FamilyHelper;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class FamilyDataDumpServiceImpl {


    @Autowired
    private FamilyRepo familyRepo;

    //saving data from excel to db
    public void save(MultipartFile file){

        try {
            List<Family> families= FamilyHelper.convertExcelToListOfFamilies(file.getInputStream());
            this.familyRepo.saveAll(families);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<Family> getAllGuests(){
        return this.familyRepo.findAll();
    }

    public ByteArrayInputStream getActualData() throws IOException {
        List<Family> familyList = this.familyRepo.findAll();
        System.out.println(familyList);
        ByteArrayInputStream stream = FamilyHelper.dataToExcel(familyList);
        return stream;
    }

}
