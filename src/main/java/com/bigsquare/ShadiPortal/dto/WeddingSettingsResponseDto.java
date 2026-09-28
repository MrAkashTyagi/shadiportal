package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WeddingSettingsResponseDto {

    private Integer id;

    private String partnerName;

    private LocalDateTime weddingDateTime;
}
