package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.WeddingSettingsRequestDto;
import com.bigsquare.ShadiPortal.dto.WeddingSettingsResponseDto;
import com.bigsquare.ShadiPortal.services.WeddingSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wedding-settings")
@CrossOrigin("*")
public class WeddingSettingsController {

    private final WeddingSettingsService
            weddingSettingsService;

    public WeddingSettingsController(
            WeddingSettingsService
                    weddingSettingsService
    ) {

        this.weddingSettingsService =
                weddingSettingsService;
    }

    @GetMapping
    public ResponseEntity<
            WeddingSettingsResponseDto
            > getSettings() {

        return ResponseEntity.ok(
                weddingSettingsService
                        .getCurrentUserSettings()
        );
    }

    @PutMapping
    public ResponseEntity<
            WeddingSettingsResponseDto
            > saveSettings(
            @RequestBody
            WeddingSettingsRequestDto request
    ) {

        return ResponseEntity.ok(
                weddingSettingsService
                        .saveOrUpdateSettings(
                                request
                        )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void>
    deleteSettings() {

        weddingSettingsService
                .deleteSettings();

        return ResponseEntity
                .noContent()
                .build();
    }
}
