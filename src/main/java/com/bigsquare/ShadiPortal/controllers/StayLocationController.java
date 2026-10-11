package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.StayLocationRequest;
import com.bigsquare.ShadiPortal.dto.StayLocationResponse;
import com.bigsquare.ShadiPortal.enums.StayLocationType;
import com.bigsquare.ShadiPortal.services.StayLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stay-locations")
@CrossOrigin(
        origins = "http://localhost:4200"
)
public class StayLocationController {

    private final StayLocationService
            stayLocationService;

    public StayLocationController(
            StayLocationService stayLocationService
    ) {

        this.stayLocationService =
                stayLocationService;
    }

    @PostMapping
    public ResponseEntity<StayLocationResponse>
    createLocation(
            @RequestBody
            StayLocationRequest request
    ) {

        StayLocationResponse response =
                stayLocationService
                        .createLocation(
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

    @GetMapping
    public ResponseEntity<List<StayLocationResponse>>
    getLocations(
            @RequestParam(
                    required = false
            )
            StayLocationType type,

            @RequestParam(
                    required = false,
                    defaultValue = "false"
            )
            Boolean activeOnly
    ) {

        return ResponseEntity.ok(
                stayLocationService
                        .getLocations(
                                type,
                                activeOnly
                        )
        );
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<StayLocationResponse>
    getLocationById(
            @PathVariable
            Integer locationId
    ) {

        return ResponseEntity.ok(
                stayLocationService
                        .getLocationById(
                                locationId
                        )
        );
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<StayLocationResponse>
    updateLocation(
            @PathVariable
            Integer locationId,

            @RequestBody
            StayLocationRequest request
    ) {

        return ResponseEntity.ok(
                stayLocationService
                        .updateLocation(
                                locationId,
                                request
                        )
        );
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void>
    deleteLocation(
            @PathVariable
            Integer locationId
    ) {

        stayLocationService
                .deleteLocation(
                        locationId
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}
