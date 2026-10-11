package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.StayRoomRequest;
import com.bigsquare.ShadiPortal.dto.StayRoomResponse;
import com.bigsquare.ShadiPortal.services.StayRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stay-rooms")
@CrossOrigin(
        origins = "http://localhost:4200"
)
public class StayRoomController {

    private final StayRoomService stayRoomService;

    public StayRoomController(
            StayRoomService stayRoomService
    ) {

        this.stayRoomService =
                stayRoomService;
    }

    @PostMapping
    public ResponseEntity<StayRoomResponse>
    createRoom(
            @RequestBody
            StayRoomRequest request
    ) {

        StayRoomResponse response =
                stayRoomService.createRoom(
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
    public ResponseEntity<List<StayRoomResponse>>
    getRooms(
            @RequestParam(
                    required = false
            )
            Integer locationId,

            @RequestParam(
                    required = false,
                    defaultValue = "false"
            )
            Boolean activeOnly
    ) {

        return ResponseEntity.ok(
                stayRoomService.getRooms(
                        locationId,
                        activeOnly
                )
        );
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<StayRoomResponse>
    getRoomById(
            @PathVariable
            Integer roomId
    ) {

        return ResponseEntity.ok(
                stayRoomService.getRoomById(
                        roomId
                )
        );
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<StayRoomResponse>
    updateRoom(
            @PathVariable
            Integer roomId,

            @RequestBody
            StayRoomRequest request
    ) {

        return ResponseEntity.ok(
                stayRoomService.updateRoom(
                        roomId,
                        request
                )
        );
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void>
    deleteRoom(
            @PathVariable
            Integer roomId
    ) {

        stayRoomService.deleteRoom(
                roomId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}
