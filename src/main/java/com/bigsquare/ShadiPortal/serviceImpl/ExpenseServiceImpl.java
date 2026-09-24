package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.ExpenseCategorySummaryDto;
import com.bigsquare.ShadiPortal.dto.ExpenseSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.ExpenseRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.ExpenseService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.bigsquare.ShadiPortal.services.CloudinaryService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CurrentUserService currentUserService;


//    @Override
//    public Expense createExpense(
//            Expense expense,
//            MultipartFile bill
//    ) {
//
//        Integer userId =
//                currentUserService
//                        .getCurrentUserId();
//
//
//        User user =
//                userRepo.findById(userId)
//                        .orElseThrow(() ->
//                                new EntityNotFoundException(
//                                        "User not found with id: "
//                                                + userId
//                                )
//                        );
//
//        expense.setUser(user);
//
//        if (
//                expense.getPaidAmount() != null &&
//                        expense.getTotalAmount() != null &&
//                        expense.getPaidAmount().compareTo(
//                                expense.getTotalAmount()
//                        ) > 0
//        ) {
//
//            throw new IllegalArgumentException(
//                    "Paid Amount cannot be greater than Total Amount"
//            );
//        }
//
//        try {
//
//            if (bill != null && !bill.isEmpty()) {
//
//                String originalFileName =
//                        bill.getOriginalFilename();
//
//                String safeFileName =
//                        System.currentTimeMillis()
//                                + "_"
//                                + (
//                                originalFileName != null
//                                        ? originalFileName
//                                        : "bill"
//                        );
//
//                Path uploadPath =
//                        Paths.get(
//                                "uploads",
//                                "bills"
//                        );
//
//                Files.createDirectories(
//                        uploadPath
//                );
//
//                Path filePath =
//                        uploadPath.resolve(
//                                safeFileName
//                        );
//
//                bill.transferTo(
//                        filePath
//                );
//
//                expense.setBillPath(
//                        filePath.toString()
//                );
//            }
//
//            return expenseRepo.save(
//                    expense
//            );
//
//        } catch (IOException exception) {
//
//            throw new RuntimeException(
//                    "Error uploading bill",
//                    exception
//            );
//        }
//    }

    @Override
    public Expense createExpense(
            Expense expense,
            MultipartFile bill
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        User user =
                userRepo
                        .findById(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                        );

        validateAmounts(expense);

        expense.setUser(user);

        Map uploadResult = null;

        try {

            if (
                    bill != null
                            && !bill.isEmpty()
            ) {

                String billFolder =
                        getBillFolder(userId);

                uploadResult =
                        cloudinaryService
                                .uploadFile(
                                        bill,
                                        billFolder
                                );

                applyBillMetadata(
                        expense,
                        bill,
                        uploadResult
                );
            }

            return expenseRepo.save(
                    expense
            );

        } catch (RuntimeException exception) {

            /*
             * Cloudinary upload ho gaya,
             * lekin database save fail hua,
             * to uploaded file rollback kar do.
             */
            if (uploadResult != null) {

                try {

                    rollbackUpload(
                            uploadResult
                    );

                } catch (
                        RuntimeException
                                rollbackException
                ) {

                    exception.addSuppressed(
                            rollbackException
                    );
                }
            }

            throw exception;
        }
    }

//
//    @Override
//    public Expense updateExpense(Integer id, Expense expense, MultipartFile bill) {
//
//        if (
//                expense.getPaidAmount() != null &&
//                        expense.getTotalAmount() != null &&
//                        expense.getPaidAmount().compareTo(
//                                expense.getTotalAmount()
//                        ) > 0
//        ) {
//            throw new RuntimeException(
//                    "Paid Amount cannot be greater than Total Amount"
//            );
//        }
//
//        Expense existingExpense =
//                expenseRepo.findById(id)
//                        .orElseThrow(() ->
//                                new EntityNotFoundException(
//                                        "Expense not found with id : " + id
//                                ));
//
//        existingExpense.setExpenseName(
//                expense.getExpenseName()
//        );
//
//        existingExpense.setCategory(
//                expense.getCategory()
//        );
//
//        existingExpense.setDescription(
//                expense.getDescription()
//        );
//
//        existingExpense.setTotalAmount(
//                expense.getTotalAmount()
//        );
//
//        existingExpense.setPaidAmount(
//                expense.getPaidAmount()
//        );
//
//        existingExpense.setPaidBy(
//                expense.getPaidBy()
//        );
////
////        existingExpense.setBillPath(
////                expense.getBillPath()
////        );
//
//        try {
//
//            if (bill != null && !bill.isEmpty()) {
//
//                // Purani file delete karo
//                if (
//                        existingExpense.getBillPath() != null &&
//                                !existingExpense.getBillPath().isBlank()
//                ) {
//
//                    Files.deleteIfExists(
//                            Paths.get(
//                                    existingExpense.getBillPath()
//                            )
//                    );
//
//                }
//
//                String fileName =
//                        bill.getOriginalFilename();
//
//                Path uploadPath =
//                        Paths.get("uploads/bills");
//
//                Files.createDirectories(uploadPath);
//
//                Path filePath =
//                        uploadPath.resolve(fileName);
//
//                bill.transferTo(filePath);
//
//                existingExpense.setBillPath(
//                        filePath.toString()
//                );
//            }
//
//        } catch (IOException e) {
//
//            throw new RuntimeException(
//                    "Error updating bill",
//                    e
//            );
//
//        }
//
//        existingExpense.setExpenseDate(
//                expense.getExpenseDate()
//        );
//
//        return expenseRepo.save(existingExpense);
//    }

@Override
public Expense updateExpense(
        Integer id,
        Expense expense,
        MultipartFile bill
) {

    validateAmounts(expense);

    Expense existingExpense =
            getOwnedExpense(id);

    existingExpense.setExpenseName(
            expense.getExpenseName()
    );

    existingExpense.setCategory(
            expense.getCategory()
    );

    existingExpense.setDescription(
            expense.getDescription()
    );

    existingExpense.setTotalAmount(
            expense.getTotalAmount()
    );

    existingExpense.setPaidAmount(
            expense.getPaidAmount()
    );

    existingExpense.setPaidBy(
            expense.getPaidBy()
    );

    existingExpense.setExpenseDate(
            expense.getExpenseDate()
    );

    boolean replacingBill =
            bill != null
                    && !bill.isEmpty();

    String previousPublicId =
            existingExpense.getBillPublicId();

    String previousResourceType =
            existingExpense.getBillResourceType();

    String previousBillPath =
            existingExpense.getBillPath();

    Map newUploadResult = null;

    try {

        if (replacingBill) {

            Integer userId =
                    currentUserService
                            .getCurrentUserId();

            newUploadResult =
                    cloudinaryService
                            .uploadFile(
                                    bill,
                                    getBillFolder(userId)
                            );

            applyBillMetadata(
                    existingExpense,
                    bill,
                    newUploadResult
            );
        }

        Expense savedExpense =
                expenseRepo.save(
                        existingExpense
                );

        if (replacingBill) {

            if (
                    previousPublicId != null
                            && !previousPublicId.isBlank()
            ) {

                cloudinaryService.deleteFile(
                        previousPublicId,
                        previousResourceType != null
                                && !previousResourceType.isBlank()
                                ? previousResourceType
                                : "image"
                );

            } else if (
                    previousBillPath != null
                            && !previousBillPath.isBlank()
            ) {

                try {

                    java.nio.file.Files
                            .deleteIfExists(
                                    java.nio.file.Paths
                                            .get(
                                                    previousBillPath
                                            )
                            );

                } catch (Exception exception) {

                    System.err.println(
                            "Legacy bill file delete nahi hui: "
                                    + exception.getMessage()
                    );
                }
            }
        }

        return savedExpense;

    } catch (RuntimeException exception) {

        if (newUploadResult != null) {

            try {

                rollbackUpload(
                        newUploadResult
                );

            } catch (
                    RuntimeException
                            rollbackException
            ) {

                exception.addSuppressed(
                        rollbackException
                );
            }
        }

        throw exception;
    }
}

    @Override
    public Expense getExpenseById(Integer id) {

        return expenseRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Expense not found with id : " + id
                        ));
    }

    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepo.findAll();
    }

    @Override
    public Page<Expense> getPaginatedExpenses(
            int page,
            int size,
            String search,
            String category
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id").descending()
                );

        String searchValue =
                search == null
                        ? ""
                        : search.trim();

        String categoryValue =
                category == null
                        ? ""
                        : category.trim();

        return expenseRepo.findBySearchAndCategory(
                userId,
                searchValue,
                categoryValue,
                pageable
        );
    }

    @Override
    public void deleteExpense(Integer id) {

        Expense expense =
                expenseRepo.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Expense not found")
                        );

        try {

            if (
                    expense.getBillPath() != null &&
                            !expense.getBillPath().isBlank()
            ) {

                Path filePath =
                        Paths.get(expense.getBillPath());

                Files.deleteIfExists(filePath);

            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to delete bill file: "
                            + e.getMessage()
            );

        }

        expenseRepo.delete(expense);
    }

    @Override
    public byte[] exportExpenses(
            String search,
            String category
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        String searchValue =
                search == null
                        ? ""
                        : search.trim();

        String categoryValue =
                category == null
                        ? ""
                        : category.trim();

        List<Expense> expenses =
                expenseRepo.findForExport(
                        userId,
                        searchValue,
                        categoryValue
                );

        try (
                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet(
                            "Expenses"
                    );

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue(
                            "Expense Name"
                    );

            header.createCell(1)
                    .setCellValue(
                            "Category"
                    );

            header.createCell(2)
                    .setCellValue(
                            "Description"
                    );

            header.createCell(3)
                    .setCellValue(
                            "Total Amount"
                    );

            header.createCell(4)
                    .setCellValue(
                            "Paid Amount"
                    );

            header.createCell(5)
                    .setCellValue(
                            "Pending Amount"
                    );

            header.createCell(6)
                    .setCellValue(
                            "Expense Date"
                    );

            header.createCell(7)
                    .setCellValue(
                            "Paid By"
                    );

            int rowNum = 1;

            for (Expense expense : expenses) {

                Row row =
                        sheet.createRow(
                                rowNum++
                        );

                row.createCell(0)
                        .setCellValue(
                                expense.getExpenseName() != null
                                        ? expense.getExpenseName()
                                        : ""
                        );

                row.createCell(1)
                        .setCellValue(
                                expense.getCategory() != null
                                        ? expense.getCategory()
                                        : ""
                        );

                row.createCell(2)
                        .setCellValue(
                                expense.getDescription() != null
                                        ? expense.getDescription()
                                        : ""
                        );

                double totalAmount =
                        expense.getTotalAmount() != null
                                ? expense.getTotalAmount()
                                : 0.0;

                double paidAmount =
                        expense.getPaidAmount() != null
                                ? expense.getPaidAmount()
                                : 0.0;

                double pendingAmount =
                        totalAmount - paidAmount;

                row.createCell(3)
                        .setCellValue(
                                totalAmount
                        );

                row.createCell(4)
                        .setCellValue(
                                paidAmount
                        );

                row.createCell(5)
                        .setCellValue(
                                pendingAmount
                        );

                row.createCell(6)
                        .setCellValue(
                                expense.getExpenseDate() != null
                                        ? expense.getExpenseDate()
                                        .toString()
                                        : ""
                        );

                row.createCell(7)
                        .setCellValue(
                                expense.getPaidBy() != null
                                        ? expense.getPaidBy()
                                        : ""
                        );
            }

            for (
                    int columnIndex = 0;
                    columnIndex <= 7;
                    columnIndex++
            ) {

                sheet.autoSizeColumn(
                        columnIndex
                );
            }

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Error while exporting expenses",
                    exception
            );
        }
    }

    @Override
    public ExpenseSummaryDto getExpenseSummary() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        List<Expense> expenses =
                expenseRepo.findAllByUserId(
                        userId
                );

        Double totalExpense = expenses.stream()
                .map(Expense::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(0.0, Double::sum);

        Double totalPaidExpense = expenses.stream()
                .map(Expense::getPaidAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(0.0, Double::sum);

        Double totalPendingExpense =
                totalExpense - totalPaidExpense;

        Long totalExpenses = (long) expenses.size();

        Double highestExpense = expenses.stream()
                .map(Expense::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .max(Double::compareTo)
                .orElse(0.0);

        String topCategory = expenses.stream()
                .filter(expense ->
                        expense.getCategory() != null &&
                                !expense.getCategory().isBlank() &&
                                expense.getTotalAmount() != null
                )
                .collect(
                        java.util.stream.Collectors.groupingBy(
                                Expense::getCategory,
                                java.util.stream.Collectors.summingDouble(
                                        Expense::getTotalAmount
                                )
                        )
                )
                .entrySet()
                .stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("-");

        return new ExpenseSummaryDto(
                totalExpense,
                totalPaidExpense,
                totalPendingExpense,
                totalExpenses,
                highestExpense,
                topCategory
        );
    }

    @Override
    public List<ExpenseCategorySummaryDto>
    getExpenseCategorySummary() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return expenseRepo
                .getCategoryWiseExpense(
                        userId
                )
                .stream()
                .map(row -> new ExpenseCategorySummaryDto(

                        String.valueOf(row[0]),

                        row[1] != null
                                ? BigDecimal.valueOf(
                                ((Number) row[1]).doubleValue()
                        )
                                : BigDecimal.ZERO

                ))
                .toList();
    }

    private void validateAmounts(
            Expense expense
    ) {

        if (
                expense.getPaidAmount() != null
                        && expense.getTotalAmount() != null
                        && expense.getPaidAmount()
                        .compareTo(
                                expense.getTotalAmount()
                        ) > 0
        ) {

            throw new IllegalArgumentException(
                    "Paid Amount cannot be greater "
                            + "than Total Amount"
            );
        }
    }

    private Expense getOwnedExpense(
            Integer expenseId
    ) {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        return expenseRepo
                .findByIdAndUserId(
                        expenseId,
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Expense not found with id: "
                                        + expenseId
                        )
                );
    }

    private String getBillFolder(
            Integer userId
    ) {

        return "users/"
                + userId
                + "/bills";
    }

    private void applyBillMetadata(
            Expense expense,
            MultipartFile bill,
            Map uploadResult
    ) {

        Object secureUrl =
                uploadResult.get(
                        "secure_url"
                );

        Object publicId =
                uploadResult.get(
                        "public_id"
                );

        Object resourceType =
                uploadResult.get(
                        "resource_type"
                );

        if (secureUrl == null || publicId == null) {

            throw new IllegalStateException(
                    "Cloudinary upload response is incomplete"
            );
        }

        expense.setBillUrl(
                String.valueOf(secureUrl)
        );

        expense.setBillPublicId(
                String.valueOf(publicId)
        );

        expense.setBillResourceType(
                resourceType != null
                        ? String.valueOf(
                        resourceType
                )
                        : "image"
        );

        expense.setBillOriginalName(
                bill.getOriginalFilename()
        );

        expense.setBillContentType(
                bill.getContentType()
        );

        /*
         * Compatibility stage:
         * new Cloudinary records ke liye local path null.
         */
        expense.setBillPath(null);
    }

    private boolean hasCloudinaryBill(
            Expense expense
    ) {

        return expense.getBillPublicId() != null
                && !expense.getBillPublicId()
                .isBlank();
    }

    private void deleteCloudinaryBill(
            Expense expense
    ) {

        if (!hasCloudinaryBill(expense)) {
            return;
        }

        String resourceType =
                expense.getBillResourceType();

        if (
                resourceType == null
                        || resourceType.isBlank()
        ) {

            resourceType = "image";
        }

        cloudinaryService.deleteFile(
                expense.getBillPublicId(),
                resourceType
        );
    }

    private void rollbackUpload(
            Map uploadResult
    ) {

        if (uploadResult == null) {
            return;
        }

        Object publicId =
                uploadResult.get(
                        "public_id"
                );

        if (publicId == null) {
            return;
        }

        Object resourceType =
                uploadResult.get(
                        "resource_type"
                );

        cloudinaryService.deleteFile(
                String.valueOf(publicId),
                resourceType != null
                        ? String.valueOf(resourceType)
                        : "image"
        );
    }



}
