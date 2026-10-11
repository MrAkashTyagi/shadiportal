package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.BulkStayAllocationRequest;
import com.bigsquare.ShadiPortal.dto.StayAllocationRequest;
import com.bigsquare.ShadiPortal.dto.StayAllocationResponse;
import com.bigsquare.ShadiPortal.dto.StayRoomCapacityResponse;
import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.entities.StayAllocation;
import com.bigsquare.ShadiPortal.entities.StayLocation;
import com.bigsquare.ShadiPortal.entities.StayRoom;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.enums.StayAllocationStatus;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.repositories.StayAllocationRepo;
import com.bigsquare.ShadiPortal.repositories.StayRoomRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.StayAllocationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class StayAllocationServiceImpl
        implements StayAllocationService {

    private static final Set<StayAllocationStatus>
            ACTIVE_STATUSES =
            EnumSet.of(
                    StayAllocationStatus.PLANNED,
                    StayAllocationStatus.CHECKED_IN
            );

    private final StayAllocationRepo stayAllocationRepo;

    private final StayRoomRepo stayRoomRepo;

    private final GuestRepo guestRepo;

    private final UserRepo userRepo;

    private final CurrentUserService currentUserService;

    public StayAllocationServiceImpl(
            StayAllocationRepo stayAllocationRepo,
            StayRoomRepo stayRoomRepo,
            GuestRepo guestRepo,
            UserRepo userRepo,
            CurrentUserService currentUserService
    ) {
        this.stayAllocationRepo =
                stayAllocationRepo;

        this.stayRoomRepo =
                stayRoomRepo;

        this.guestRepo =
                guestRepo;

        this.userRepo =
                userRepo;

        this.currentUserService =
                currentUserService;
    }

    @Override
    @Transactional
    public StayAllocationResponse allocateGuest(
            StayAllocationRequest request
    ) {
        Integer userId =
                getCurrentUserId();

        User currentUser =
                getUserById(userId);

        validateRequest(request);

        Guest guest =
                getGuestByIdAndUserId(
                        request.getGuestId(),
                        userId
                );

        StayRoom room =
                getRoomByIdAndUserId(
                        request.getRoomId(),
                        userId
                );

        validateGuestRequiresStay(guest);

        validateGuestHasNoActiveAllocation(
                guest.getId(),
                userId,
                null
        );

        validateRoomHasCapacity(
                room,
                userId,
                1L
        );

        StayAllocation allocation =
                buildAllocation(
                        guest,
                        room,
                        currentUser,
                        request.getCheckInDate(),
                        request.getCheckOutDate(),
                        request.getNotes()
                );

        StayAllocation savedAllocation =
                stayAllocationRepo.save(
                        allocation
                );

        return mapToResponse(
                savedAllocation
        );
    }

    @Override
    @Transactional
    public List<StayAllocationResponse> allocateGuests(
            BulkStayAllocationRequest request
    ) {
        Integer userId =
                getCurrentUserId();

        User currentUser =
                getUserById(userId);

        validateBulkRequest(request);

        StayRoom room =
                getRoomByIdAndUserId(
                        request.getRoomId(),
                        userId
                );

        List<Integer> uniqueGuestIds =
                request.getGuestIds()
                        .stream()
                        .distinct()
                        .toList();

        List<Guest> guests =
                uniqueGuestIds.stream()
                        .map(
                                guestId ->
                                        getGuestByIdAndUserId(
                                                guestId,
                                                userId
                                        )
                        )
                        .toList();

        for (Guest guest : guests) {

            validateGuestRequiresStay(guest);

            validateGuestHasNoActiveAllocation(
                    guest.getId(),
                    userId,
                    null
            );
        }

        validateRoomHasCapacity(
                room,
                userId,
                (long) guests.size()
        );

        List<StayAllocation> allocations =
                guests.stream()
                        .map(
                                guest ->
                                        buildAllocation(
                                                guest,
                                                room,
                                                currentUser,
                                                request.getCheckInDate(),
                                                request.getCheckOutDate(),
                                                request.getNotes()
                                        )
                        )
                        .toList();

        return stayAllocationRepo
                .saveAll(allocations)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StayAllocationResponse> getAllocations(
            Integer locationId,
            Integer roomId,
            StayAllocationStatus status
    ) {
        Integer userId =
                getCurrentUserId();

        if (roomId != null) {
            getRoomByIdAndUserId(
                    roomId,
                    userId
            );
        }

        List<StayAllocation> allocations =
                stayAllocationRepo
                        .findAllocationsWithFilters(
                                userId,
                                locationId,
                                roomId,
                                status
                        );

        return allocations.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StayAllocationResponse getAllocationById(
            Integer allocationId
    ) {
        Integer userId =
                getCurrentUserId();

        StayAllocation allocation =
                getAllocationByIdAndUserId(
                        allocationId,
                        userId
                );

        return mapToResponse(
                allocation
        );
    }

    @Override
    @Transactional
    public StayAllocationResponse updateAllocation(
            Integer allocationId,
            StayAllocationRequest request
    ) {
        Integer userId =
                getCurrentUserId();

        validateRequest(request);

        StayAllocation allocation =
                getAllocationByIdAndUserId(
                        allocationId,
                        userId
                );

        validateAllocationCanBeModified(
                allocation
        );

        Guest requestedGuest =
                getGuestByIdAndUserId(
                        request.getGuestId(),
                        userId
                );

        StayRoom requestedRoom =
                getRoomByIdAndUserId(
                        request.getRoomId(),
                        userId
                );

        validateGuestRequiresStay(
                requestedGuest
        );

        validateGuestHasNoActiveAllocation(
                requestedGuest.getId(),
                userId,
                allocationId
        );

        boolean roomChanged =
                !allocation
                        .getRoom()
                        .getId()
                        .equals(
                                requestedRoom.getId()
                        );

        if (roomChanged) {
            validateRoomHasCapacity(
                    requestedRoom,
                    userId,
                    1L
            );
        }

        allocation.setGuest(
                requestedGuest
        );

        allocation.setRoom(
                requestedRoom
        );

        allocation.setCheckInDate(
                request.getCheckInDate()
        );

        allocation.setCheckOutDate(
                request.getCheckOutDate()
        );

        allocation.setNotes(
                cleanText(
                        request.getNotes()
                )
        );

        StayAllocation updatedAllocation =
                stayAllocationRepo.save(
                        allocation
                );

        return mapToResponse(
                updatedAllocation
        );
    }

    @Override
    @Transactional
    public StayAllocationResponse updateStatus(
            Integer allocationId,
            StayAllocationStatus status
    ) {
        Integer userId =
                getCurrentUserId();

        if (status == null) {
            throw new IllegalArgumentException(
                    "Allocation status is required."
            );
        }

        StayAllocation allocation =
                getAllocationByIdAndUserId(
                        allocationId,
                        userId
                );

        validateStatusTransition(
                allocation.getStatus(),
                status
        );

        allocation.setStatus(
                status
        );

        StayAllocation updatedAllocation =
                stayAllocationRepo.save(
                        allocation
                );

        return mapToResponse(
                updatedAllocation
        );
    }

    @Override
    @Transactional
    public StayAllocationResponse cancelAllocation(
            Integer allocationId
    ) {
        return updateStatus(
                allocationId,
                StayAllocationStatus.CANCELLED
        );
    }

    @Override
    @Transactional
    public void deleteAllocation(
            Integer allocationId
    ) {
        Integer userId =
                getCurrentUserId();

        StayAllocation allocation =
                getAllocationByIdAndUserId(
                        allocationId,
                        userId
                );

        if (
                allocation.getStatus()
                        == StayAllocationStatus.CHECKED_IN
        ) {
            throw new IllegalArgumentException(
                    "Checked-in allocation cannot be deleted."
            );
        }

        stayAllocationRepo.delete(
                allocation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public StayRoomCapacityResponse getRoomCapacity(
            Integer roomId
    ) {
        Integer userId =
                getCurrentUserId();

        StayRoom room =
                getRoomByIdAndUserId(
                        roomId,
                        userId
                );

        long occupiedBeds =
                getOccupiedBeds(
                        room.getId(),
                        userId
                );

        long availableBeds =
                Math.max(
                        room.getCapacity()
                                - occupiedBeds,
                        0L
                );

        StayLocation location =
                room.getLocation();

        return new StayRoomCapacityResponse(
                room.getId(),
                room.getName(),
                location.getId(),
                location.getName(),
                room.getCapacity(),
                occupiedBeds,
                availableBeds,
                availableBeds == 0
        );
    }

    private StayAllocation buildAllocation(
            Guest guest,
            StayRoom room,
            User user,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            String notes
    ) {
        StayAllocation allocation =
                new StayAllocation();

        allocation.setGuest(
                guest
        );

        allocation.setRoom(
                room
        );

        allocation.setUser(
                user
        );

        allocation.setStatus(
                StayAllocationStatus.PLANNED
        );

        allocation.setCheckInDate(
                checkInDate
        );

        allocation.setCheckOutDate(
                checkOutDate
        );

        allocation.setNotes(
                cleanText(notes)
        );

        return allocation;
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
                .findById(userId)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                );
    }

    private Guest getGuestByIdAndUserId(
            Integer guestId,
            Integer userId
    ) {
        if (guestId == null) {
            throw new IllegalArgumentException(
                    "Guest id is required."
            );
        }

        return guestRepo
                .findByIdAndUserId(
                        guestId,
                        userId
                )
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Guest not found with id: "
                                                + guestId
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

    private StayAllocation getAllocationByIdAndUserId(
            Integer allocationId,
            Integer userId
    ) {
        if (allocationId == null) {
            throw new IllegalArgumentException(
                    "Stay allocation id is required."
            );
        }

        return stayAllocationRepo
                .findByIdAndUserId(
                        allocationId,
                        userId
                )
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Stay allocation not found with id: "
                                                + allocationId
                                )
                );
    }

    private void validateRequest(
            StayAllocationRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Stay allocation request is required."
            );
        }

        if (request.getGuestId() == null) {
            throw new IllegalArgumentException(
                    "Guest id is required."
            );
        }

        if (request.getRoomId() == null) {
            throw new IllegalArgumentException(
                    "Stay room id is required."
            );
        }

        validateDates(
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        validateNotes(
                request.getNotes()
        );
    }

    private void validateBulkRequest(
            BulkStayAllocationRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Bulk stay allocation request is required."
            );
        }

        if (
                request.getGuestIds() == null
                        || request.getGuestIds().isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "At least one guest is required."
            );
        }

        if (
                request.getGuestIds()
                        .stream()
                        .anyMatch(id -> id == null)
        ) {
            throw new IllegalArgumentException(
                    "Guest ids cannot contain null values."
            );
        }

        if (request.getRoomId() == null) {
            throw new IllegalArgumentException(
                    "Stay room id is required."
            );
        }

        validateDates(
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        validateNotes(
                request.getNotes()
        );
    }

    private void validateDates(
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        if (
                checkInDate != null
                        && checkOutDate != null
                        && checkOutDate.isBefore(checkInDate)
        ) {
            throw new IllegalArgumentException(
                    "Check-out date cannot be before check-in date."
            );
        }
    }

    private void validateNotes(
            String notes
    ) {
        if (
                notes != null
                        && notes.trim().length() > 1000
        ) {
            throw new IllegalArgumentException(
                    "Notes cannot exceed 1000 characters."
            );
        }
    }

    private void validateGuestRequiresStay(
            Guest guest
    ) {
        if (
                guest.getStay() == null
                        || !guest.getStay()
                        .equalsIgnoreCase("Yes")
        ) {
            throw new IllegalArgumentException(
                    "Guest does not require stay."
            );
        }
    }

    private void validateGuestHasNoActiveAllocation(
            Integer guestId,
            Integer userId,
            Integer ignoredAllocationId
    ) {
        stayAllocationRepo
                .findFirstByGuestIdAndUserIdAndStatusInOrderByIdDesc(
                        guestId,
                        userId,
                        ACTIVE_STATUSES
                )
                .ifPresent(
                        activeAllocation -> {

                            boolean sameAllocation =
                                    ignoredAllocationId != null
                                            && activeAllocation
                                            .getId()
                                            .equals(
                                                    ignoredAllocationId
                                            );

                            if (!sameAllocation) {
                                throw new IllegalArgumentException(
                                        "Guest already has an active stay allocation."
                                );
                            }
                        }
                );
    }

    private void validateRoomHasCapacity(
            StayRoom room,
            Integer userId,
            long requestedBeds
    ) {
        if (!Boolean.TRUE.equals(room.getActive())) {
            throw new IllegalArgumentException(
                    "Inactive room cannot receive guest allocations."
            );
        }

        if (
                !Boolean.TRUE.equals(
                        room.getLocation().getActive()
                )
        ) {
            throw new IllegalArgumentException(
                    "Inactive stay location cannot receive guest allocations."
            );
        }

        long occupiedBeds =
                getOccupiedBeds(
                        room.getId(),
                        userId
                );

        long availableBeds =
                room.getCapacity()
                        - occupiedBeds;

        if (requestedBeds > availableBeds) {
            throw new IllegalArgumentException(
                    "Room "
                            + room.getName()
                            + " has only "
                            + Math.max(
                            availableBeds,
                            0L
                    )
                            + " available bed(s)."
            );
        }
    }

    private long getOccupiedBeds(
            Integer roomId,
            Integer userId
    ) {
        return stayAllocationRepo
                .countByRoomIdAndUserIdAndStatusIn(
                        roomId,
                        userId,
                        ACTIVE_STATUSES
                );
    }

    private void validateAllocationCanBeModified(
            StayAllocation allocation
    ) {
        if (
                allocation.getStatus()
                        == StayAllocationStatus.CHECKED_OUT
        ) {
            throw new IllegalArgumentException(
                    "Checked-out allocation cannot be modified."
            );
        }

        if (
                allocation.getStatus()
                        == StayAllocationStatus.CANCELLED
        ) {
            throw new IllegalArgumentException(
                    "Cancelled allocation cannot be modified."
            );
        }
    }

    private void validateStatusTransition(
            StayAllocationStatus currentStatus,
            StayAllocationStatus requestedStatus
    ) {
        if (currentStatus == requestedStatus) {
            return;
        }

        boolean allowed;

        switch (currentStatus) {

            case PLANNED ->
                    allowed =
                            requestedStatus
                                    == StayAllocationStatus.CHECKED_IN
                                    || requestedStatus
                                    == StayAllocationStatus.CANCELLED;

            case CHECKED_IN ->
                    allowed =
                            requestedStatus
                                    == StayAllocationStatus.CHECKED_OUT
                                    || requestedStatus
                                    == StayAllocationStatus.CANCELLED;

            case CHECKED_OUT, CANCELLED ->
                    allowed = false;

            default ->
                    allowed = false;
        }

        if (!allowed) {
            throw new IllegalArgumentException(
                    "Invalid allocation status transition from "
                            + currentStatus
                            + " to "
                            + requestedStatus
                            + "."
            );
        }
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

    private StayAllocationResponse mapToResponse(
            StayAllocation allocation
    ) {
        Guest guest =
                allocation.getGuest();

        Family family =
                guest.getFamily();

        StayRoom room =
                allocation.getRoom();

        StayLocation location =
                room.getLocation();

        return new StayAllocationResponse(
                allocation.getId(),
                guest.getId(),
                guest.getName(),
                family == null
                        ? null
                        : family.getId(),
                family == null
                        ? null
                        : family.getFamilyName(),
                room.getId(),
                room.getName(),
                room.getCapacity(),
                location.getId(),
                location.getName(),
                location.getType(),
                allocation.getStatus(),
                allocation.getCheckInDate(),
                allocation.getCheckOutDate(),
                allocation.getNotes(),
                allocation.getCreatedAt(),
                allocation.getUpdatedAt()
        );
    }
}
