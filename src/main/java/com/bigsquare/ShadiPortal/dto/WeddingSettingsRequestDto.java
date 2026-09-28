package com.bigsquare.ShadiPortal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WeddingSettingsRequestDto {

    private String partnerName;

    private LocalDateTime weddingDateTime;
}
