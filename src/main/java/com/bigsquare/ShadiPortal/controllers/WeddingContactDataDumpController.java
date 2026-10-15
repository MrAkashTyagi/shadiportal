package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.helper.WeddingContactHelper;
import com.bigsquare.ShadiPortal.serviceImpl.WeddingContactDataDumpServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/wedding-contacts/dump")
@RequiredArgsConstructor
public class WeddingContactDataDumpController {

    private final WeddingContactDataDumpServiceImpl
            weddingContactDataDumpService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file")
            MultipartFile file
    ) {

        if (
                !WeddingContactHelper
                        .checkExcelFormat(file)
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Please upload a valid .xlsx Excel file"
                            )
                    );
        }

        int importedCount =
                weddingContactDataDumpService
                        .save(file);

        return ResponseEntity
                .ok(
                        Map.of(
                                "message",
                                "Wedding contacts imported successfully",
                                "importedCount",
                                importedCount
                        )
                );
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadExcel()
            throws IOException {

        ByteArrayInputStream stream =
                weddingContactDataDumpService
                        .getActualData();

        InputStreamResource file =
                new InputStreamResource(
                        stream
                );

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"wedding-contacts.xlsx\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
}
