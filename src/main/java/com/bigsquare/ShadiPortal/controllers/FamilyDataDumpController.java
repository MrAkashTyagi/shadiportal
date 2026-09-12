package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.helper.FamilyHelper;
import com.bigsquare.ShadiPortal.serviceImpl.FamilyDataDumpServiceImpl;
import com.bigsquare.ShadiPortal.serviceImpl.FamilyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/dataDump")
public class FamilyDataDumpController {

    @Autowired
    private FamilyDataDumpServiceImpl familyDataDumpService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Integer userId
    ){

        if (FamilyHelper.checkExcelFormat(file)){

            //upload
            this.familyDataDumpService.save(file, userId);
            return ResponseEntity.ok(Map.of("message", "File is uploaded successfully !! Data is saved to db !!"));

        }else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file only");

        }

    }

    @GetMapping("/family")
    public List<Family> getAllProducts(
            @RequestParam Integer userId
    ){

        return familyDataDumpService
                .getAllFamilies(
                        userId
                );
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadExcel(
            @RequestParam Integer userId
    ) throws IOException {

        String fileName =
                "families.xlsx";

        ByteArrayInputStream actualData =
                familyDataDumpService.getActualData(
                        userId
                );

        InputStreamResource file =
                new InputStreamResource(
                        actualData
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }


}
