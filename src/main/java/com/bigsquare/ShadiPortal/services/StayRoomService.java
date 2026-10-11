package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.StayRoomRequest;
import com.bigsquare.ShadiPortal.dto.StayRoomResponse;

import java.util.List;

public interface StayRoomService {

    StayRoomResponse createRoom(
            StayRoomRequest request
    );

    List<StayRoomResponse> getRooms(
            Integer locationId,
            Boolean activeOnly
    );

    StayRoomResponse getRoomById(
            Integer roomId
    );

    StayRoomResponse updateRoom(
            Integer roomId,
            StayRoomRequest request
    );

    void deleteRoom(
            Integer roomId
    );
}
