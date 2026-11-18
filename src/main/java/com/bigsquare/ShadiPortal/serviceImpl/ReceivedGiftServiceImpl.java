package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.ReceivedGiftRequest;
import com.bigsquare.ShadiPortal.dto.ReceivedGiftResponse;
import com.bigsquare.ShadiPortal.dto.ReceivedGiftSummaryResponse;
import com.bigsquare.ShadiPortal.entities.ReceivedGift;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.enums.ReceivedGiftType;
import com.bigsquare.ShadiPortal.repositories.ReceivedGiftRepository;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.ReceivedGiftService;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReceivedGiftServiceImpl
        implements ReceivedGiftService {

    private final ReceivedGiftRepository repository;

    private final UserRepo userRepo;

    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ReceivedGiftResponse create(
            ReceivedGiftRequest request
    ) {

        validateRequest(request);

        Integer userId =
                getCurrentUserId();

        User user =
                getUserById(userId);

        ReceivedGift entity =
                ReceivedGift.builder()
                        .giverName(
                                request
                                        .getGiverName()
                                        .trim()
                        )
                        .mobileNumber(
                                cleanText(
                                        request.getMobileNumber()
                                )
                        )
                        .giftType(
                                request.getGiftType()
                        )
                        .cashAmount(
                                resolveCashAmount(
                                        request
                                )
                        )
                        .giftDescription(
                                resolveGiftDescription(
                                        request
                                )
                        )
                        .quantity(
                                normalizeQuantity(
                                        request.getQuantity()
                                )
                        )
                        .notes(
                                cleanText(
                                        request.getNotes()
                                )
                        )
                        .receivedAt(
                                request.getReceivedAt()
                        )
                        .user(user)
                        .build();

        ReceivedGift savedEntity =
                repository.saveAndFlush(
                        entity
                );

        return mapToResponse(
                savedEntity
        );
    }

    @Override
    @Transactional
    public ReceivedGiftResponse update(
            Integer id,
            ReceivedGiftRequest request
    ) {

        validateRequest(request);

        Integer userId =
                getCurrentUserId();

        ReceivedGift existingEntity =
                getOwnedEntity(
                        id,
                        userId
                );

        existingEntity.setGiverName(
                request
                        .getGiverName()
                        .trim()
        );

        existingEntity.setMobileNumber(
                cleanText(
                        request.getMobileNumber()
                )
        );

        existingEntity.setGiftType(
                request.getGiftType()
        );

        existingEntity.setCashAmount(
                resolveCashAmount(
                        request
                )
        );

        existingEntity.setGiftDescription(
                resolveGiftDescription(
                        request
                )
        );

        existingEntity.setQuantity(
                normalizeQuantity(
                        request.getQuantity()
                )
        );

        existingEntity.setNotes(
                cleanText(
                        request.getNotes()
                )
        );

        if (request.getReceivedAt() != null) {

            existingEntity.setReceivedAt(
                    request.getReceivedAt()
            );
        }

        ReceivedGift updatedEntity =
                repository.saveAndFlush(
                        existingEntity
                );

        return mapToResponse(
                updatedEntity
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReceivedGiftResponse getById(
            Integer id
    ) {

        Integer userId =
                getCurrentUserId();

        ReceivedGift entity =
                getOwnedEntity(
                        id,
                        userId
                );

        return mapToResponse(
                entity
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceivedGiftResponse> getAll(
            int page,
            int size,
            String search,
            ReceivedGiftType giftType,
            BigDecimal minCashAmount,
            BigDecimal maxCashAmount
    ) {

        validatePagination(
                page,
                size
        );

        validateCashAmountRange(
                minCashAmount,
                maxCashAmount
        );

        Integer userId =
                getCurrentUserId();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Order.desc(
                                        "receivedAt"
                                ),
                                Sort.Order.desc(
                                        "id"
                                )
                        )
                );

        return repository
                .findWithFilters(
                        userId,
                        normalizeSearch(
                                search
                        ),
                        giftType,
                        minCashAmount,
                        maxCashAmount,
                        pageable
                )
                .map(
                        this::mapToResponse
                );
    }

    @Override
    @Transactional
    public void delete(
            Integer id
    ) {

        Integer userId =
                getCurrentUserId();

        ReceivedGift entity =
                getOwnedEntity(
                        id,
                        userId
                );

        repository.delete(
                entity
        );

        repository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public ReceivedGiftSummaryResponse getSummary() {

        Integer userId =
                getCurrentUserId();

        long totalRecords =
                repository.countByUserId(
                        userId
                );

        Long totalQuantity =
                repository
                        .getTotalQuantityByUserId(
                                userId
                        );

        Long cashGiftRecords =
                repository
                        .countCashGiftsByUserId(
                                userId
                        );

        BigDecimal totalCashReceived =
                repository
                        .getTotalCashReceivedByUserId(
                                userId
                        );

        Long physicalGiftQuantity =
                repository
                        .getPhysicalGiftQuantityByUserId(
                                userId
                        );

        return ReceivedGiftSummaryResponse
                .builder()
                .totalRecords(
                        totalRecords
                )
                .totalQuantity(
                        totalQuantity != null
                                ? totalQuantity
                                : 0L
                )
                .cashGiftRecords(
                        cashGiftRecords != null
                                ? cashGiftRecords
                                : 0L
                )
                .totalCashReceived(
                        totalCashReceived != null
                                ? totalCashReceived
                                : BigDecimal.ZERO
                )
                .physicalGiftQuantity(
                        physicalGiftQuantity != null
                                ? physicalGiftQuantity
                                : 0L
                )
                .build();
    }

    private ReceivedGift getOwnedEntity(
            Integer id,
            Integer userId
    ) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "Received gift id is required"
            );
        }

        return repository
                .findByIdAndUserId(
                        id,
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Received gift not found with id: "
                                        + id
                        )
                );
    }

    private User getUserById(
            Integer userId
    ) {

        return userRepo
                .findById(
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );
    }

    private Integer getCurrentUserId() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        if (userId == null) {

            throw new IllegalStateException(
                    "Current user not found"
            );
        }

        return userId;
    }

    private void validateRequest(
            ReceivedGiftRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Received gift request is required"
            );
        }

        validateGiverName(
                request.getGiverName()
        );

        validateGiftType(
                request.getGiftType()
        );

        validateMobileNumber(
                request.getMobileNumber()
        );

        validateQuantity(
                request.getQuantity()
        );

        validateNotes(
                request.getNotes()
        );

        validateGiftFields(
                request
        );
    }

    private void validateGiverName(
            String giverName
    ) {

        if (
                giverName == null
                        || giverName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Giver name is required"
            );
        }

        if (
                giverName
                        .trim()
                        .length() > 150
        ) {

            throw new IllegalArgumentException(
                    "Giver name cannot exceed 150 characters"
            );
        }
    }

    private void validateGiftType(
            ReceivedGiftType giftType
    ) {

        if (giftType == null) {

            throw new IllegalArgumentException(
                    "Gift type is required"
            );
        }
    }

    private void validateMobileNumber(
            String mobileNumber
    ) {

        if (
                mobileNumber != null
                        && mobileNumber
                        .trim()
                        .length() > 20
        ) {

            throw new IllegalArgumentException(
                    "Mobile number cannot exceed 20 characters"
            );
        }
    }

    private void validateQuantity(
            Integer requestedQuantity
    ) {

        int quantity =
                normalizeQuantity(
                        requestedQuantity
                );

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (quantity > 1000) {

            throw new IllegalArgumentException(
                    "Quantity cannot exceed 1000"
            );
        }
    }

    private void validateNotes(
            String notes
    ) {

        if (
                notes != null
                        && notes
                        .trim()
                        .length() > 1000
        ) {

            throw new IllegalArgumentException(
                    "Notes cannot exceed 1000 characters"
            );
        }
    }

    private void validateGiftFields(
            ReceivedGiftRequest request
    ) {

        if (
                request.getGiftType()
                        == ReceivedGiftType.CASH
        ) {

            validateCashGift(
                    request
            );

            return;
        }

        validatePhysicalGift(
                request
        );
    }

    private void validateCashGift(
            ReceivedGiftRequest request
    ) {

        if (request.getCashAmount() == null) {

            throw new IllegalArgumentException(
                    "Cash amount is required for a cash gift"
            );
        }

        if (
                request
                        .getCashAmount()
                        .compareTo(
                                BigDecimal.ZERO
                        ) <= 0
        ) {

            throw new IllegalArgumentException(
                    "Cash amount must be greater than zero"
            );
        }
    }

    private void validatePhysicalGift(
            ReceivedGiftRequest request
    ) {

        if (
                request.getGiftDescription() == null
                        || request
                        .getGiftDescription()
                        .isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Gift description is required for a physical gift"
            );
        }

        if (
                request
                        .getGiftDescription()
                        .trim()
                        .length() > 500
        ) {

            throw new IllegalArgumentException(
                    "Gift description cannot exceed 500 characters"
            );
        }
    }

    private void validatePagination(
            int page,
            int size
    ) {

        if (page < 0) {

            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (
                size <= 0
                        || size > 100
        ) {

            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }
    }

    private void validateCashAmountRange(
            BigDecimal minCashAmount,
            BigDecimal maxCashAmount
    ) {

        if (
                minCashAmount != null
                        && minCashAmount.compareTo(
                        BigDecimal.ZERO
                ) < 0
        ) {

            throw new IllegalArgumentException(
                    "Minimum cash amount cannot be negative"
            );
        }

        if (
                maxCashAmount != null
                        && maxCashAmount.compareTo(
                        BigDecimal.ZERO
                ) < 0
        ) {

            throw new IllegalArgumentException(
                    "Maximum cash amount cannot be negative"
            );
        }

        if (
                minCashAmount != null
                        && maxCashAmount != null
                        && minCashAmount.compareTo(
                        maxCashAmount
                ) > 0
        ) {

            throw new IllegalArgumentException(
                    "Minimum cash amount cannot be greater than maximum cash amount"
            );
        }
    }

    private BigDecimal resolveCashAmount(
            ReceivedGiftRequest request
    ) {

        if (
                request.getGiftType()
                        != ReceivedGiftType.CASH
        ) {

            return null;
        }

        return request.getCashAmount();
    }

    private String resolveGiftDescription(
            ReceivedGiftRequest request
    ) {

        if (
                request.getGiftType()
                        == ReceivedGiftType.CASH
        ) {

            return null;
        }

        return cleanText(
                request.getGiftDescription()
        );
    }

    private int normalizeQuantity(
            Integer quantity
    ) {

        return quantity == null
                ? 1
                : quantity;
    }

    private String normalizeSearch(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    private String cleanText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String cleanedValue =
                value.trim();

        return cleanedValue.isBlank()
                ? null
                : cleanedValue;
    }

    private ReceivedGiftResponse mapToResponse(
            ReceivedGift entity
    ) {

        return ReceivedGiftResponse
                .builder()
                .id(
                        entity.getId()
                )
                .giverName(
                        entity.getGiverName()
                )
                .mobileNumber(
                        entity.getMobileNumber()
                )
                .giftType(
                        entity.getGiftType()
                )
                .cashAmount(
                        entity.getCashAmount()
                )
                .giftDescription(
                        entity.getGiftDescription()
                )
                .quantity(
                        entity.getQuantity()
                )
                .notes(
                        entity.getNotes()
                )
                .receivedAt(
                        entity.getReceivedAt()
                )
                .createdAt(
                        entity.getCreatedAt()
                )
                .updatedAt(
                        entity.getUpdatedAt()
                )
                .build();
    }
}
