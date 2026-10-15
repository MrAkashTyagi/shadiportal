package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.helper.WeddingTaskHelper;
import com.bigsquare.ShadiPortal.serviceImpl.WeddingTaskDataDumpServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/wedding-tasks/dump")
@CrossOrigin(
        origins = {
                "http://localhost:4200",
                "http://shadiportal-frontend-2026.s3-website-ap-southeast-2.amazonaws.com"
        }
)
@RequiredArgsConstructor
public class WeddingTaskDataDumpController {

    private final WeddingTaskDataDumpServiceImpl
            weddingTaskDataDumpService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file")
            MultipartFile file
    ) {

        System.out.println("TASK IMPORT HIT");
        System.out.println("FILE = " + file.getOriginalFilename());

        try {

            int importedCount =
                    weddingTaskDataDumpService.save(file);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Wedding tasks imported successfully",
                            "importedCount",
                            importedCount
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw e;
        }
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
