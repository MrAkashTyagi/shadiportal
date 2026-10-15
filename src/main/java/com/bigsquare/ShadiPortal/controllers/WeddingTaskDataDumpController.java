package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.helper.WeddingTaskHelper;
import com.bigsquare.ShadiPortal.serviceImpl.WeddingTaskDataDumpServiceImpl;
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
@RequestMapping("/api/wedding-tasks/dump")
@RequiredArgsConstructor
public class WeddingTaskDataDumpController {

    private final WeddingTaskDataDumpServiceImpl
            weddingTaskDataDumpService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file")
            MultipartFile file
    ) {

        if (
                !WeddingTaskHelper
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
                weddingTaskDataDumpService
                        .save(file);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Wedding tasks imported successfully",
                        "importedCount",
                        importedCount
                )
        );
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadExcel()
            throws IOException {

        ByteArrayInputStream stream =
                weddingTaskDataDumpService
                        .getActualData();

        InputStreamResource file =
                new InputStreamResource(stream);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"wedding-tasks.xlsx\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
}
