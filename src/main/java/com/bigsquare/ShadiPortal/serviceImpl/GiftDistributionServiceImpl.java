package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.GiftDistributionRequest;
import com.bigsquare.ShadiPortal.dto.GiftDistributionResponse;
import com.bigsquare.ShadiPortal.dto.GiftDistributionSummaryResponse;
import com.bigsquare.ShadiPortal.entities.GiftDistribution;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.enums.GiftType;
import com.bigsquare.ShadiPortal.repositories.GiftDistributionRepository;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.GiftDistributionService;
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
public class GiftDistributionServiceImpl
        implements GiftDistributionService {

    private final GiftDistributionRepository repository;

    private final UserRepo userRepo;

    private final CurrentUserService currentUserService;

    private final GiftDistributionExpenseSyncService
            expenseSyncService;

    @Override
    @Transactional
    public GiftDistributionResponse create(
            GiftDistributionRequest request
    ) {

        validateRequest(request);

        Integer userId =
                getCurrentUserId();

        User user =
                getUserById(userId);

        GiftDistribution entity =
                GiftDistribution.builder()
                        .recipientName(
                                request
                                        .getRecipientName()
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
                        .amount(
                                request.getAmount()
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
                        .user(user)
                        .build();

        GiftDistribution savedEntity =
                repository.saveAndFlush(
                        entity
                );

        expenseSyncService
                .syncGiftDistributionExpense(
                        userId
                );

        return mapToResponse(
                savedEntity
        );
    }

    @Override
    @Transactional
    public GiftDistributionResponse update(
            Integer id,
            GiftDistributionRequest request
    ) {

        validateRequest(request);

        Integer userId =
                getCurrentUserId();

        GiftDistribution entity =
                getOwnedEntity(
                        id,
                        userId
                );

        entity.setRecipientName(
                request
                        .getRecipientName()
                        .trim()
        );

        entity.setMobileNumber(
                cleanText(
                        request.getMobileNumber()
                )
        );

        entity.setGiftType(
                request.getGiftType()
        );

        entity.setAmount(
                request.getAmount()
        );

        entity.setQuantity(
                normalizeQuantity(
                        request.getQuantity()
                )
        );

        entity.setNotes(
                cleanText(
                        request.getNotes()
                )
        );

        GiftDistribution updatedEntity =
                repository.saveAndFlush(
                        entity
                );

        expenseSyncService
                .syncGiftDistributionExpense(
                        userId
                );

        return mapToResponse(
                updatedEntity
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GiftDistributionResponse getById(
            Integer id
    ) {

        Integer userId =
                getCurrentUserId();

        GiftDistribution entity =
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
    public Page<GiftDistributionResponse> getAll(
            int page,
            int size,
            String search,
            GiftType giftType,
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {

        validatePagination(
                page,
                size
        );

        validateAmountRange(
                minAmount,
                maxAmount
        );

        Integer userId =
                getCurrentUserId();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Order.desc("id")
                        )
                );

        return repository
                .findWithFilters(
                        userId,
                        normalizeSearch(search),
                        giftType,
                        minAmount,
                        maxAmount,
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

        GiftDistribution entity =
                getOwnedEntity(
                        id,
                        userId
                );

        repository.delete(
                entity
        );

        repository.flush();

        expenseSyncService
                .syncGiftDistributionExpense(
                        userId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public GiftDistributionSummaryResponse getSummary() {

        Integer userId =
                getCurrentUserId();

        BigDecimal totalAmount =
                repository
                        .getTotalAmountByUserId(
                                userId
                        );

        Long totalQuantity =
                repository
                        .getTotalQuantityByUserId(
                                userId
                        );

        long totalRecords =
                repository.countByUserId(
                        userId
                );

        return GiftDistributionSummaryResponse
                .builder()
                .totalRecords(
                        totalRecords
                )
                .totalQuantity(
                        totalQuantity != null
                                ? totalQuantity
                                : 0L
                )
                .totalAmount(
                        totalAmount != null
                                ? totalAmount
                                : BigDecimal.ZERO
                )
                .build();
    }

    private GiftDistribution getOwnedEntity(
            Integer id,
            Integer userId
    ) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "Gift distribution id is required"
            );
        }

        return repository
                .findByIdAndUserId(
                        id,
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Gift distribution not found with id: "
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
            GiftDistributionRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Gift distribution request is required"
            );
        }

        if (
                request.getRecipientName() == null
                        || request
                        .getRecipientName()
                        .isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Recipient name is required"
            );
        }

        if (
                request
                        .getRecipientName()
                        .trim()
                        .length() > 150
        ) {

            throw new IllegalArgumentException(
                    "Recipient name cannot exceed 150 characters"
            );
        }

        if (request.getGiftType() == null) {

            throw new IllegalArgumentException(
                    "Gift type is required"
            );
        }

        if (request.getAmount() == null) {

            throw new IllegalArgumentException(
                    "Amount is required"
            );
        }

        if (
                request
                        .getAmount()
                        .compareTo(
                                BigDecimal.ZERO
                        ) < 0
        ) {

            throw new IllegalArgumentException(
                    "Amount cannot be negative"
            );
        }

        int quantity =
                normalizeQuantity(
                        request.getQuantity()
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

        if (
                request.getMobileNumber() != null
                        && request
                        .getMobileNumber()
                        .trim()
                        .length() > 20
        ) {

            throw new IllegalArgumentException(
                    "Mobile number cannot exceed 20 characters"
            );
        }

        if (
                request.getNotes() != null
                        && request
                        .getNotes()
                        .trim()
                        .length() > 1000
        ) {

            throw new IllegalArgumentException(
                    "Notes cannot exceed 1000 characters"
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

    private void validateAmountRange(
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {

        if (
                minAmount != null
                        && minAmount.compareTo(
                        BigDecimal.ZERO
                ) < 0
        ) {

            throw new IllegalArgumentException(
                    "Minimum amount cannot be negative"
            );
        }

        if (
                maxAmount != null
                        && maxAmount.compareTo(
                        BigDecimal.ZERO
                ) < 0
        ) {

            throw new IllegalArgumentException(
                    "Maximum amount cannot be negative"
            );
        }

        if (
                minAmount != null
                        && maxAmount != null
                        && minAmount.compareTo(
                        maxAmount
                ) > 0
        ) {

            throw new IllegalArgumentException(
                    "Minimum amount cannot be greater than maximum amount"
            );
        }
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

    private GiftDistributionResponse mapToResponse(
            GiftDistribution entity
    ) {

        return GiftDistributionResponse
                .builder()
                .id(
                        entity.getId()
                )
                .recipientName(
                        entity.getRecipientName()
                )
                .mobileNumber(
                        entity.getMobileNumber()
                )
                .giftType(
                        entity.getGiftType()
                )
                .amount(
                        entity.getAmount()
                )
                .quantity(
                        entity.getQuantity()
                )
                .totalAmount(
                        entity.getTotalAmount()
                )
                .notes(
                        entity.getNotes()
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
