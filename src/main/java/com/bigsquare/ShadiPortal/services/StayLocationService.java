package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.StayLocationRequest;
import com.bigsquare.ShadiPortal.dto.StayLocationResponse;
import com.bigsquare.ShadiPortal.enums.StayLocationType;

import java.util.List;

public interface StayLocationService {

    StayLocationResponse createLocation(
            StayLocationRequest request
    );

    List<StayLocationResponse> getLocations(
            StayLocationType type,
            Boolean activeOnly
    );

    StayLocationResponse getLocationById(
            Integer locationId
    );

    StayLocationResponse updateLocation(
            Integer locationId,
            StayLocationRequest request
    );

    void deleteLocation(
            Integer locationId
    );
}
