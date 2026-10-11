package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.StayRoomRequest;
import com.bigsquare.ShadiPortal.dto.StayRoomResponse;
import com.bigsquare.ShadiPortal.entities.StayLocation;
import com.bigsquare.ShadiPortal.entities.StayRoom;
import com.bigsquare.ShadiPortal.repositories.StayLocationRepo;
import com.bigsquare.ShadiPortal.repositories.StayRoomRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.StayRoomService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StayRoomServiceImpl implements StayRoomService {

    private final StayRoomRepo stayRoomRepo;

    private final StayLocationRepo stayLocationRepo;

    private final CurrentUserService currentUserService;

    public StayRoomServiceImpl(
            StayRoomRepo stayRoomRepo,
            StayLocationRepo stayLocationRepo,
            CurrentUserService currentUserService
    ) {
        this.stayRoomRepo = stayRoomRepo;
        this.stayLocationRepo = stayLocationRepo;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public StayRoomResponse createRoom(
            StayRoomRequest request
    ) {
        Integer userId = getCurrentUserId();

        validateRequest(request);

        StayLocation location =
                getLocationByIdAndUserId(
                        request.getLocationId(),
                        userId
                );

        String normalizedName =
                request.getName().trim();

        boolean duplicateExists =
                stayRoomRepo
                        .existsByLocationIdAndNameIgnoreCase(
                                location.getId(),
                                normalizedName
                        );

        if (duplicateExists) {
            throw new IllegalArgumentException(
                    "A room with this name already exists at this location."
            );
        }

        StayRoom room = new StayRoom();

        applyRequest(
                room,
                request
        );

        room.setLocation(location);

        StayRoom savedRoom =
                stayRoomRepo.save(room);

        return mapToResponse(savedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StayRoomResponse> getRooms(
            Integer locationId,
            Boolean activeOnly
    ) {
        Integer userId = getCurrentUserId();

        List<StayRoom> rooms;

        if (locationId != null) {
            getLocationByIdAndUserId(
                    locationId,
                    userId
            );

            if (Boolean.TRUE.equals(activeOnly)) {
                rooms =
                        stayRoomRepo
                                .findAllByLocationIdAndLocationUserIdAndActiveTrueOrderByNameAsc(
                                        locationId,
                                        userId
                                );
            } else {
                rooms =
                        stayRoomRepo
                                .findAllByLocationIdAndLocationUserIdOrderByNameAsc(
                                        locationId,
                                        userId
                                );
            }
        } else {
            rooms =
                    stayRoomRepo
                            .findAllByLocationUserIdOrderByLocationNameAscNameAsc(
                                    userId
                            );

            if (Boolean.TRUE.equals(activeOnly)) {
                rooms =
                        rooms.stream()
                                .filter(
                                        room ->
                                                Boolean.TRUE.equals(
                                                        room.getActive()
                                                )
                                )
                                .toList();
            }
        }

        return rooms.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StayRoomResponse getRoomById(
            Integer roomId
    ) {
        Integer userId = getCurrentUserId();

        StayRoom room =
                getRoomByIdAndUserId(
                        roomId,
                        userId
                );

        return mapToResponse(room);
    }

    @Override
    @Transactional
    public StayRoomResponse updateRoom(
            Integer roomId,
            StayRoomRequest request
    ) {
        Integer userId = getCurrentUserId();

        validateRequest(request);

        StayRoom existingRoom =
                getRoomByIdAndUserId(
                        roomId,
                        userId
                );

        StayLocation requestedLocation =
                getLocationByIdAndUserId(
                        request.getLocationId(),
                        userId
                );

        String normalizedName =
                request.getName().trim();

        boolean duplicateExists =
                stayRoomRepo
                        .existsByLocationIdAndNameIgnoreCaseAndIdNot(
                                requestedLocation.getId(),
                                normalizedName,
                                roomId
                        );

        if (duplicateExists) {
            throw new IllegalArgumentException(
                    "A room with this name already exists at this location."
            );
        }

        applyRequest(
                existingRoom,
                request
        );

        existingRoom.setLocation(
                requestedLocation
        );

        StayRoom updatedRoom =
                stayRoomRepo.save(
                        existingRoom
                );

        return mapToResponse(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(
            Integer roomId
    ) {
        Integer userId = getCurrentUserId();

        StayRoom room =
                getRoomByIdAndUserId(
                        roomId,
                        userId
                );

        stayRoomRepo.delete(room);
    }

    private Integer getCurrentUserId() {
        Integer userId =
                currentUserService
                        .getCurrentUserId();

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User id is required."
            );
        }

        return userId;
    }

    private StayLocation getLocationByIdAndUserId(
            Integer locationId,
            Integer userId
    ) {
        if (locationId == null) {
            throw new IllegalArgumentException(
                    "Stay location id is required."
            );
        }

        return stayLocationRepo
                .findByIdAndUserId(
                        locationId,
                        userId
                )
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Stay location not found with id: "
                                                + locationId
                                )
                );
    }

    private StayRoom getRoomByIdAndUserId(
            Integer roomId,
            Integer userId
    ) {
        if (roomId == null) {
            throw new IllegalArgumentException(
                    "Stay room id is required."
            );
        }

        return stayRoomRepo
                .findByIdAndLocationUserId(
                        roomId,
                        userId
                )
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Stay room not found with id: "
                                                + roomId
                                )
                );
    }

    private void validateRequest(
            StayRoomRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Stay room request is required."
            );
        }

        if (request.getLocationId() == null) {
            throw new IllegalArgumentException(
                    "Stay location id is required."
            );
        }

        if (
                request.getName() == null
                        || request.getName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Room name is required."
            );
        }

        if (
                request.getName()
                        .trim()
                        .length() > 120
        ) {
            throw new IllegalArgumentException(
                    "Room name cannot exceed 120 characters."
            );
        }

        if (request.getCapacity() == null) {
            throw new IllegalArgumentException(
                    "Room capacity is required."
            );
        }

        if (request.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                    "Room capacity must be greater than zero."
            );
        }

        if (request.getCapacity() > 500) {
            throw new IllegalArgumentException(
                    "Room capacity cannot exceed 500."
            );
        }

        if (
                request.getFloor() != null
                        && request.getFloor()
                        .trim()
                        .length() > 50
        ) {
            throw new IllegalArgumentException(
                    "Floor cannot exceed 50 characters."
            );
        }

        if (
                request.getNotes() != null
                        && request.getNotes()
                        .trim()
                        .length() > 1000
        ) {
            throw new IllegalArgumentException(
                    "Notes cannot exceed 1000 characters."
            );
        }
    }

    private void applyRequest(
            StayRoom room,
            StayRoomRequest request
    ) {
        room.setName(
                request.getName().trim()
        );

        room.setCapacity(
                request.getCapacity()
        );

        room.setFloor(
                cleanText(
                        request.getFloor()
                )
        );

        room.setNotes(
                cleanText(
                        request.getNotes()
                )
        );

        room.setActive(
                request.getActive() == null
                        ? Boolean.TRUE
                        : request.getActive()
        );
    }

    private String cleanText(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String cleanedValue =
                value.trim();

        return cleanedValue.isEmpty()
                ? null
                : cleanedValue;
    }

    private StayRoomResponse mapToResponse(
            StayRoom room
    ) {
        StayLocation location =
                room.getLocation();

        return new StayRoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getFloor(),
                room.getNotes(),
                room.getActive(),
                location.getId(),
                location.getName(),
                location.getType()
        );
    }
}
