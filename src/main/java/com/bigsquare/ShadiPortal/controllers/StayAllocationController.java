package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.BulkStayAllocationRequest;
import com.bigsquare.ShadiPortal.dto.StayAllocationRequest;
import com.bigsquare.ShadiPortal.dto.StayAllocationResponse;
import com.bigsquare.ShadiPortal.dto.StayRoomCapacityResponse;
import com.bigsquare.ShadiPortal.enums.StayAllocationStatus;
import com.bigsquare.ShadiPortal.services.StayAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/stay-allocations")
@CrossOrigin(origins = {"http://localhost:4200",
        "http://shadiportal-frontend-2026.s3-website-ap-southeast-2.amazonaws.com"
})
public class StayAllocationController {

    private final StayAllocationService stayAllocationService;

    public StayAllocationController(
            StayAllocationService stayAllocationService
    ) {
        this.stayAllocationService =
                stayAllocationService;
    }

    @PostMapping
    public ResponseEntity<StayAllocationResponse>
    allocateGuest(
            @RequestBody
            StayAllocationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        stayAllocationService
                                .allocateGuest(request)
                );
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<StayAllocationResponse>>
    allocateGuests(
            @RequestBody
            BulkStayAllocationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        stayAllocationService
                                .allocateGuests(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<StayAllocationResponse>>
    getAllocations(
            @RequestParam(required = false)
            Integer locationId,

            @RequestParam(required = false)
            Integer roomId,

            @RequestParam(required = false)
            StayAllocationStatus status
    ) {
        return ResponseEntity.ok(
                stayAllocationService
                        .getAllocations(
                                locationId,
                                roomId,
                                status
                        )
        );
    }

    @GetMapping("/{allocationId}")
    public ResponseEntity<StayAllocationResponse>
    getAllocationById(
            @PathVariable
            Integer allocationId
    ) {
        return ResponseEntity.ok(
                stayAllocationService
                        .getAllocationById(
                                allocationId
                        )
        );
    }

    @PutMapping("/{allocationId}")
    public ResponseEntity<StayAllocationResponse>
    updateAllocation(
            @PathVariable
            Integer allocationId,

            @RequestBody
            StayAllocationRequest request
    ) {
        return ResponseEntity.ok(
                stayAllocationService
                        .updateAllocation(
                                allocationId,
                                request
                        )
        );
    }

    @PatchMapping("/{allocationId}/status")
    public ResponseEntity<StayAllocationResponse>
    updateStatus(
            @PathVariable
            Integer allocationId,

            @RequestParam
            StayAllocationStatus status
    ) {
        return ResponseEntity.ok(
                stayAllocationService
                        .updateStatus(
                                allocationId,
                                status
                        )
        );
    }

    @PatchMapping("/{allocationId}/cancel")
    public ResponseEntity<StayAllocationResponse>
    cancelAllocation(
            @PathVariable
            Integer allocationId
    ) {
        return ResponseEntity.ok(
                stayAllocationService
                        .cancelAllocation(
                                allocationId
                        )
        );
    }

    @GetMapping("/rooms/{roomId}/capacity")
    public ResponseEntity<StayRoomCapacityResponse>
    getRoomCapacity(
            @PathVariable
            Integer roomId
    ) {
        return ResponseEntity.ok(
                stayAllocationService
                        .getRoomCapacity(
                                roomId
                        )
        );
    }

    @DeleteMapping("/{allocationId}")
    public ResponseEntity<Void>
    deleteAllocation(
            @PathVariable
            Integer allocationId
    ) {
        stayAllocationService
                .deleteAllocation(
                        allocationId
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/export")
    public ResponseEntity<Resource>
    exportStayAllocations()
            throws IOException {

        ByteArrayInputStream excelData =
                stayAllocationService
                        .exportStayAllocations();

        InputStreamResource file =
                new InputStreamResource(
                        excelData
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=stay-allocation-report.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }

}
