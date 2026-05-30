package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.helper.GuestHelper;
import com.bigsquare.ShadiPortal.serviceImpl.GuestDataDumpServiceImpl;
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
@RequestMapping("/datadump")
@CrossOrigin("*")
public class GuestDataDumpController {


    @Autowired
    private GuestDataDumpServiceImpl guestDataDumpService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file){

        if (GuestHelper.checkExcelFormat(file)){

            //upload
            this.guestDataDumpService.save(file);
            return ResponseEntity.ok(Map.of("message", "File is uploaded successfully !! Data is saved to db !!"));

        }else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file only");

        }

    }

    @GetMapping("/guest")
    public List<Guest> getAllProducts(){
        return this.guestDataDumpService.getAllGuests();
    }


    @RequestMapping("/download")
    public ResponseEntity<Resource> downloadExcel() throws IOException {

        String fileName = "products.xlsx";
        ByteArrayInputStream actualData = this.guestDataDumpService.getActualData();
        InputStreamResource file = new InputStreamResource(actualData);
        ResponseEntity<Resource> body = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; fileName"+fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);

        return body;
    }



}
