package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.GiftDistributionRequest;
import com.bigsquare.ShadiPortal.dto.GiftDistributionResponse;
import com.bigsquare.ShadiPortal.dto.GiftDistributionSummaryResponse;
import com.bigsquare.ShadiPortal.enums.GiftType;
import com.bigsquare.ShadiPortal.services.GiftDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/gift-distributions")
@RequiredArgsConstructor
public class GiftDistributionController {

    private final GiftDistributionService service;

    @PostMapping
    public ResponseEntity<GiftDistributionResponse> create(
            @RequestBody
            GiftDistributionRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.create(request)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiftDistributionResponse> update(
            @PathVariable
            Integer id,

            @RequestBody
            GiftDistributionRequest request
    ) {

        return ResponseEntity.ok(
                service.update(
                        id,
                        request
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiftDistributionResponse> getById(
            @PathVariable
            Integer id
    ) {

        return ResponseEntity.ok(
                service.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<Page<GiftDistributionResponse>> getAll(
            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size,

            @RequestParam(
                    defaultValue = ""
            )
            String search,

            @RequestParam(
                    required = false
            )
            GiftType giftType,

            @RequestParam(
                    required = false
            )
            BigDecimal minAmount,

            @RequestParam(
                    required = false
            )
            BigDecimal maxAmount
    ) {

        return ResponseEntity.ok(
                service.getAll(
                        page,
                        size,
                        search,
                        giftType,
                        minAmount,
                        maxAmount
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable
            Integer id
    ) {

        service.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/summary")
    public ResponseEntity<GiftDistributionSummaryResponse>
    getSummary() {

        return ResponseEntity.ok(
                service.getSummary()
        );
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(
                    defaultValue = ""
            )
            String search,

            @RequestParam(
                    required = false
            )
            GiftType giftType,

            @RequestParam(
                    required = false
            )
            BigDecimal minAmount,

            @RequestParam(
                    required = false
            )
            BigDecimal maxAmount
    ) {

        byte[] excelData =
                service.export(
                        search,
                        giftType,
                        minAmount,
                        maxAmount
                );

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"gift-distribution.xlsx\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(
                        excelData
                );
    }
}
