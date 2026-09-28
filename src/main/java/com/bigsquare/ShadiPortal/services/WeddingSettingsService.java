package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.WeddingSettingsRequestDto;
import com.bigsquare.ShadiPortal.dto.WeddingSettingsResponseDto;

public interface WeddingSettingsService {

    WeddingSettingsResponseDto
    getCurrentUserSettings();

    WeddingSettingsResponseDto
    saveOrUpdateSettings(
            WeddingSettingsRequestDto request
    );

    void deleteSettings();
}
