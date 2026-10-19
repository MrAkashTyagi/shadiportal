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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bigsquare.ShadiPortal.dto.GiftEnvelopeSummaryItem;

import java.io.ByteArrayOutputStream;
import java.util.List;

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

        List<Object[]> envelopeSummaryRows =
                repository
                        .getEnvelopeAmountSummary(
                                userId,
                                GiftType.ENVELOPE
                        );

        List<GiftEnvelopeSummaryItem> envelopeItems =
                envelopeSummaryRows
                        .stream()
                        .map(row -> {

                            BigDecimal amount =
                                    row[0] != null
                                            ? (BigDecimal) row[0]
                                            : BigDecimal.ZERO;

                            Long count =
                                    row[1] != null
                                            ? ((Number) row[1])
                                            .longValue()
                                            : 0L;

                            BigDecimal itemTotal =
                                    amount.multiply(
                                            BigDecimal.valueOf(
                                                    count
                                            )
                                    );

                            return GiftEnvelopeSummaryItem
                                    .builder()
                                    .amount(amount)
                                    .count(count)
                                    .totalAmount(itemTotal)
                                    .build();
                        })
                        .toList();

        Long totalEnvelopes =
                envelopeItems
                        .stream()
                        .mapToLong(
                                GiftEnvelopeSummaryItem::getCount
                        )
                        .sum();

        BigDecimal envelopeGrandTotal =
                envelopeItems
                        .stream()
                        .map(
                                GiftEnvelopeSummaryItem::getTotalAmount
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return GiftDistributionSummaryResponse
                .builder()
                .totalRecords(totalRecords)
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
                .envelopeItems(envelopeItems)
                .totalEnvelopes(totalEnvelopes)
                .envelopeGrandTotal(envelopeGrandTotal)
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

    @Override
    @Transactional(readOnly = true)
    public byte[] export(
            String search,
            GiftType giftType,
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {

        validateAmountRange(
                minAmount,
                maxAmount
        );

        Integer userId =
                getCurrentUserId();

        List<GiftDistribution> gifts =
                repository.findForExport(
                        userId,
                        normalizeSearch(search),
                        giftType,
                        minAmount,
                        maxAmount
                );

        try (
                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet(
                            "Gift Distribution"
                    );

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue(
                            "Recipient Name"
                    );

            header.createCell(1)
                    .setCellValue(
                            "Mobile Number"
                    );

            header.createCell(2)
                    .setCellValue(
                            "Gift Type"
                    );

            header.createCell(3)
                    .setCellValue(
                            "Amount"
                    );

            header.createCell(4)
                    .setCellValue(
                            "Quantity"
                    );

            header.createCell(5)
                    .setCellValue(
                            "Total Amount"
                    );

            header.createCell(6)
                    .setCellValue(
                            "Notes"
                    );

            int rowNumber = 1;

            for (
                    GiftDistribution gift
                    : gifts
            ) {

                Row row =
                        sheet.createRow(
                                rowNumber++
                        );

                row.createCell(0)
                        .setCellValue(
                                gift.getRecipientName()
                        );

                row.createCell(1)
                        .setCellValue(
                                gift.getMobileNumber() != null
                                        ? gift.getMobileNumber()
                                        : ""
                        );

                row.createCell(2)
                        .setCellValue(
                                gift.getGiftType() != null
                                        ? gift.getGiftType()
                                        .name()
                                        : ""
                        );

                row.createCell(3)
                        .setCellValue(
                                gift.getAmount() != null
                                        ? gift.getAmount()
                                        .doubleValue()
                                        : 0.0
                        );

                row.createCell(4)
                        .setCellValue(
                                gift.getQuantity() != null
                                        ? gift.getQuantity()
                                        : 0
                        );

                row.createCell(5)
                        .setCellValue(
                                gift.getTotalAmount()
                                        .doubleValue()
                        );

                row.createCell(6)
                        .setCellValue(
                                gift.getNotes() != null
                                        ? gift.getNotes()
                                        : ""
                        );
            }

            for (
                    int columnIndex = 0;
                    columnIndex <= 6;
                    columnIndex++
            ) {

                sheet.autoSizeColumn(
                        columnIndex
                );
            }

            workbook.write(
                    outputStream
            );

            return outputStream
                    .toByteArray();

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Error while exporting gift distribution",
                    exception
            );
        }
    }
}
