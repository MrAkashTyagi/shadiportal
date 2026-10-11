package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.BulkStayAllocationRequest;
import com.bigsquare.ShadiPortal.dto.StayAllocationRequest;
import com.bigsquare.ShadiPortal.dto.StayAllocationResponse;
import com.bigsquare.ShadiPortal.dto.StayRoomCapacityResponse;
import com.bigsquare.ShadiPortal.enums.StayAllocationStatus;

import java.util.List;

public interface StayAllocationService {

    StayAllocationResponse allocateGuest(
            StayAllocationRequest request
    );

    List<StayAllocationResponse> allocateGuests(
            BulkStayAllocationRequest request
    );

    List<StayAllocationResponse> getAllocations(
            Integer locationId,
            Integer roomId,
            StayAllocationStatus status
    );

    StayAllocationResponse getAllocationById(
            Integer allocationId
    );

    StayAllocationResponse updateAllocation(
            Integer allocationId,
            StayAllocationRequest request
    );

    StayAllocationResponse updateStatus(
            Integer allocationId,
            StayAllocationStatus status
    );

    StayAllocationResponse cancelAllocation(
            Integer allocationId
    );

    void deleteAllocation(
            Integer allocationId
    );

    StayRoomCapacityResponse getRoomCapacity(
            Integer roomId
    );
}
