package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.StayLocationRequest;
import com.bigsquare.ShadiPortal.dto.StayLocationResponse;
import com.bigsquare.ShadiPortal.entities.StayLocation;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.enums.StayLocationType;
import com.bigsquare.ShadiPortal.repositories.StayLocationRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.StayLocationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StayLocationServiceImpl
        implements StayLocationService {

    private final StayLocationRepo stayLocationRepo;

    private final UserRepo userRepo;

    private final CurrentUserService currentUserService;

    public StayLocationServiceImpl(
            StayLocationRepo stayLocationRepo,
            UserRepo userRepo,
            CurrentUserService currentUserService
    ) {

        this.stayLocationRepo =
                stayLocationRepo;

        this.userRepo =
                userRepo;

        this.currentUserService =
                currentUserService;
    }

    @Override
    @Transactional
    public StayLocationResponse createLocation(
            StayLocationRequest request
    ) {

        Integer userId =
                getCurrentUserId();

        User currentUser =
                getUserById(
                        userId
                );

        validateRequest(
                request
        );

        String normalizedName =
                request.getName()
                        .trim();

        boolean duplicateExists =
                stayLocationRepo
                        .existsByUserIdAndNameIgnoreCase(
                                userId,
                                normalizedName
                        );

        if (duplicateExists) {

            throw new IllegalArgumentException(
                    "A stay location with this name already exists."
            );
        }

        StayLocation location =
                new StayLocation();

        applyRequest(
                location,
                request
        );

        location.setUser(
                currentUser
        );

        StayLocation savedLocation =
                stayLocationRepo.save(
                        location
                );

        return mapToResponse(
                savedLocation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StayLocationResponse> getLocations(
            StayLocationType type,
            Boolean activeOnly
    ) {

        Integer userId =
                getCurrentUserId();

        List<StayLocation> locations;

        if (type != null) {

            locations =
                    stayLocationRepo
                            .findAllByUserIdAndTypeOrderByNameAsc(
                                    userId,
                                    type
                            );

            if (
                    Boolean.TRUE.equals(
                            activeOnly
                    )
            ) {

                locations =
                        locations.stream()
                                .filter(
                                        location ->
                                                Boolean.TRUE.equals(
                                                        location.getActive()
                                                )
                                )
                                .toList();
            }

        } else if (
                Boolean.TRUE.equals(
                        activeOnly
                )
        ) {

            locations =
                    stayLocationRepo
                            .findAllByUserIdAndActiveTrueOrderByNameAsc(
                                    userId
                            );

        } else {

            locations =
                    stayLocationRepo
                            .findAllByUserIdOrderByNameAsc(
                                    userId
                            );
        }

        return locations.stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StayLocationResponse getLocationById(
            Integer locationId
    ) {

        Integer userId =
                getCurrentUserId();

        StayLocation location =
                getLocationByIdAndUserId(
                        locationId,
                        userId
                );

        return mapToResponse(
                location
        );
    }

    @Override
    @Transactional
    public StayLocationResponse updateLocation(
            Integer locationId,
            StayLocationRequest request
    ) {

        Integer userId =
                getCurrentUserId();

        validateRequest(
                request
        );

        StayLocation existingLocation =
                getLocationByIdAndUserId(
                        locationId,
                        userId
                );

        String normalizedName =
                request.getName()
                        .trim();

        boolean duplicateExists =
                stayLocationRepo
                        .existsByUserIdAndNameIgnoreCaseAndIdNot(
                                userId,
                                normalizedName,
                                locationId
                        );

        if (duplicateExists) {

            throw new IllegalArgumentException(
                    "A stay location with this name already exists."
            );
        }

        applyRequest(
                existingLocation,
                request
        );

        StayLocation updatedLocation =
                stayLocationRepo.save(
                        existingLocation
                );

        return mapToResponse(
                updatedLocation
        );
    }

    @Override
    @Transactional
    public void deleteLocation(
            Integer locationId
    ) {

        Integer userId =
                getCurrentUserId();

        StayLocation location =
                getLocationByIdAndUserId(
                        locationId,
                        userId
                );

        stayLocationRepo.delete(
                location
        );
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

    private User getUserById(
            Integer userId
    ) {

        return userRepo
                .findById(
                        userId
                )
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                );
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

    private void validateRequest(
            StayLocationRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Stay location request is required."
            );
        }

        if (
                request.getName() == null
                        || request.getName().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Location name is required."
            );
        }

        if (
                request.getName()
                        .trim()
                        .length() > 120
        ) {

            throw new IllegalArgumentException(
                    "Location name cannot exceed 120 characters."
            );
        }

        if (request.getType() == null) {

            throw new IllegalArgumentException(
                    "Location type is required."
            );
        }

        if (
                request.getAddress() != null
                        && request.getAddress()
                        .trim()
                        .length() > 500
        ) {

            throw new IllegalArgumentException(
                    "Address cannot exceed 500 characters."
            );
        }

        if (
                request.getContactPerson() != null
                        && request.getContactPerson()
                        .trim()
                        .length() > 120
        ) {

            throw new IllegalArgumentException(
                    "Contact person cannot exceed 120 characters."
            );
        }

        if (
                request.getContactNumber() != null
                        && request.getContactNumber()
                        .trim()
                        .length() > 20
        ) {

            throw new IllegalArgumentException(
                    "Contact number cannot exceed 20 characters."
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
            StayLocation location,
            StayLocationRequest request
    ) {

        location.setName(
                request.getName()
                        .trim()
        );

        location.setType(
                request.getType()
        );

        location.setAddress(
                cleanText(
                        request.getAddress()
                )
        );

        location.setContactPerson(
                cleanText(
                        request.getContactPerson()
                )
        );

        location.setContactNumber(
                cleanText(
                        request.getContactNumber()
                )
        );

        location.setNotes(
                cleanText(
                        request.getNotes()
                )
        );

        location.setActive(
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

    private StayLocationResponse mapToResponse(
            StayLocation location
    ) {

        return new StayLocationResponse(
                location.getId(),
                location.getName(),
                location.getType(),
                location.getAddress(),
                location.getContactPerson(),
                location.getContactNumber(),
                location.getNotes(),
                location.getActive()
        );
    }
}
