package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.WeddingSettingsRequestDto;
import com.bigsquare.ShadiPortal.dto.WeddingSettingsResponseDto;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.entities.WeddingSettings;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.repositories.WeddingSettingsRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.WeddingSettingsService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeddingSettingsServiceImpl
        implements WeddingSettingsService {

    @Autowired
    private WeddingSettingsRepo
            weddingSettingsRepo;

    @Autowired
    private UserRepo
            userRepo;

    @Autowired
    private CurrentUserService
            currentUserService;

    @Override
    public WeddingSettingsResponseDto
    getCurrentUserSettings() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        WeddingSettings settings =
                weddingSettingsRepo
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Wedding settings not found"
                                )
                        );

        return mapToDto(
                settings
        );
    }

    @Override
    public WeddingSettingsResponseDto
    saveOrUpdateSettings(
            WeddingSettingsRequestDto request
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        User user =
                userRepo
                        .findById(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "User not found"
                                )
                        );

        WeddingSettings settings =
                weddingSettingsRepo
                        .findByUserId(userId)
                        .orElseGet(
                                WeddingSettings::new
                        );

        settings.setUser(user);

        settings.setPartnerName(
                request.getPartnerName()
        );

        settings.setWeddingDateTime(
                request.getWeddingDateTime()
        );

        WeddingSettings saved =
                weddingSettingsRepo
                        .save(settings);

        return mapToDto(
                saved
        );
    }

    @Override
    public void deleteSettings() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        WeddingSettings settings =
                weddingSettingsRepo
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Wedding settings not found"
                                )
                        );

        weddingSettingsRepo.delete(
                settings
        );
    }

    private WeddingSettingsResponseDto
    mapToDto(
            WeddingSettings settings
    ) {

        return new WeddingSettingsResponseDto(

                settings.getId(),

                settings.getPartnerName(),

                settings.getWeddingDateTime()

        );
    }
}
