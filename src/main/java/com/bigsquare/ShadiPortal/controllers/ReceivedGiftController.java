package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.ReceivedGiftRequest;
import com.bigsquare.ShadiPortal.dto.ReceivedGiftResponse;
import com.bigsquare.ShadiPortal.dto.ReceivedGiftSummaryResponse;
import com.bigsquare.ShadiPortal.enums.ReceivedGiftType;
import com.bigsquare.ShadiPortal.services.ReceivedGiftService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/received-gifts")
@RequiredArgsConstructor
public class ReceivedGiftController {

    private final ReceivedGiftService receivedGiftService;

    @PostMapping
    public ResponseEntity<ReceivedGiftResponse> create(
            @RequestBody
            ReceivedGiftRequest request
    ) {

        ReceivedGiftResponse response =
                receivedGiftService.create(
                        request
                );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        response
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceivedGiftResponse> update(
            @PathVariable
            Integer id,

            @RequestBody
            ReceivedGiftRequest request
    ) {

        return ResponseEntity.ok(
                receivedGiftService.update(
                        id,
                        request
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceivedGiftResponse> getById(
            @PathVariable
            Integer id
    ) {

        return ResponseEntity.ok(
                receivedGiftService.getById(
                        id
                )
        );
    }

    @GetMapping
    public ResponseEntity<Page<ReceivedGiftResponse>> getAll(
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
            ReceivedGiftType giftType,

            @RequestParam(
                    required = false
            )
            BigDecimal minCashAmount,

            @RequestParam(
                    required = false
            )
            BigDecimal maxCashAmount
    ) {

        return ResponseEntity.ok(
                receivedGiftService.getAll(
                        page,
                        size,
                        search,
                        giftType,
                        minCashAmount,
                        maxCashAmount
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable
            Integer id
    ) {

        receivedGiftService.delete(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/summary")
    public ResponseEntity<ReceivedGiftSummaryResponse> getSummary() {

        return ResponseEntity.ok(
                receivedGiftService.getSummary()
        );
    }
}
