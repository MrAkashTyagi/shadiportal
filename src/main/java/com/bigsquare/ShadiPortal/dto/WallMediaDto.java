package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class WallMediaDto {

    private Integer id;

    private String originalFileName;

    private String mediaUrl;

    private String contentType;

    private String mediaType;

    private Long fileSize;

    private String caption;

    private LocalDateTime uploadedAt;

    private String uploadedByName;
}
