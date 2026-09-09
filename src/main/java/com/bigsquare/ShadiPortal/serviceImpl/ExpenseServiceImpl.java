package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.ExpenseCategorySummaryDto;
import com.bigsquare.ShadiPortal.dto.ExpenseSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.repositories.ExpenseRepo;
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

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepo expenseRepo;


    @Override
    public Expense createExpense(
            Expense expense,
            MultipartFile bill
    ) {

        if (
                expense.getPaidAmount() != null &&
                        expense.getTotalAmount() != null &&
                        expense.getPaidAmount().compareTo(
                                expense.getTotalAmount()
                        ) > 0
        ) {
            throw new RuntimeException(
                    "Paid Amount cannot be greater than Total Amount"
            );
        }

        try {

            if (bill != null && !bill.isEmpty()) {

                String fileName =
                        bill.getOriginalFilename();

                Path uploadPath =
                        Paths.get("uploads/bills");

                Files.createDirectories(uploadPath);

                Path filePath =
                        uploadPath.resolve(fileName);

                bill.transferTo(filePath);

                expense.setBillPath(
                        filePath.toString()
                );
            }


            return expenseRepo.save(expense);

        } catch (IOException e) {

            throw new RuntimeException("Error uploading bill", e);

        }
    }

    @Override
    public Expense updateExpense(Integer id, Expense expense, MultipartFile bill) {

        if (
                expense.getPaidAmount() != null &&
                        expense.getTotalAmount() != null &&
                        expense.getPaidAmount().compareTo(
                                expense.getTotalAmount()
                        ) > 0
        ) {
            throw new RuntimeException(
                    "Paid Amount cannot be greater than Total Amount"
            );
        }

        Expense existingExpense =
                expenseRepo.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Expense not found with id : " + id
                                ));

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
//
//        existingExpense.setBillPath(
//                expense.getBillPath()
//        );

        try {

            if (bill != null && !bill.isEmpty()) {

                // Purani file delete karo
                if (
                        existingExpense.getBillPath() != null &&
                                !existingExpense.getBillPath().isBlank()
                ) {

                    Files.deleteIfExists(
                            Paths.get(
                                    existingExpense.getBillPath()
                            )
                    );

                }

                String fileName =
                        bill.getOriginalFilename();

                Path uploadPath =
                        Paths.get("uploads/bills");

                Files.createDirectories(uploadPath);

                Path filePath =
                        uploadPath.resolve(fileName);

                bill.transferTo(filePath);

                existingExpense.setBillPath(
                        filePath.toString()
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error updating bill",
                    e
            );

        }

        existingExpense.setExpenseDate(
                expense.getExpenseDate()
        );

        return expenseRepo.save(existingExpense);
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
    public byte[] exportExpenses() {

        List<Expense> expenses =
                expenseRepo.findAll();

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet("Expenses");

            Row header = sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("Expense Name");

            header.createCell(1)
                    .setCellValue("Category");

            header.createCell(2)
                    .setCellValue("Description");

            header.createCell(3)
                    .setCellValue("Total Amount");

            header.createCell(4)
                    .setCellValue("Paid Amount");

            header.createCell(5)
                    .setCellValue("Pending Amount");

            header.createCell(6)
                    .setCellValue("Expense Date");

            header.createCell(7)
                    .setCellValue("Paid By");

            int rowNum = 1;

            for (Expense expense : expenses) {

                Row row =
                        sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(expense.getExpenseName());

                row.createCell(1)
                        .setCellValue(expense.getCategory());

                row.createCell(2)
                        .setCellValue(expense.getDescription());

                row.createCell(3)
                        .setCellValue(
                                expense.getTotalAmount() != null
                                        ? expense.getTotalAmount().doubleValue()
                                        : 0
                        );

                row.createCell(4)
                        .setCellValue(
                                expense.getPaidAmount() != null
                                        ? expense.getPaidAmount().doubleValue()
                                        : 0
                        );

                row.createCell(5)
                        .setCellValue(
                                expense.getTotalAmount() != null
                                        && expense.getPaidAmount() != null
                                        ? expense.getTotalAmount()
                                          - expense.getPaidAmount()
                                        : 0
                        );

                row.createCell(6)
                        .setCellValue(
                                expense.getExpenseDate() != null
                                        ? expense.getExpenseDate().toString()
                                        : ""
                        );

                row.createCell(7)
                        .setCellValue(expense.getPaidBy());
            }

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while exporting expenses",
                    e
            );
        }
    }

    @Override
    public ExpenseSummaryDto getExpenseSummary() {

        List<Expense> expenses = expenseRepo.findAll();

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

//        String topCategory = expenses.stream()
//                .filter(e -> e.getCategory() != null)
//                .collect(
//                        java.util.stream.Collectors.groupingBy(
//                                Expense::getCategory,
//                                java.util.stream.Collectors.counting()
//                        )
//                )
//                .entrySet()
//                .stream()
//                .max(java.util.Map.Entry.comparingByValue())
//                .map(java.util.Map.Entry::getKey)
//                .orElse("-");

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
//
//    @Override
//    public List<ExpenseCategorySummaryDto>
//    getExpenseCategorySummary() {
//
//        return expenseRepo
//                .getCategoryWiseExpense()
//                .stream()
//                .map(row -> new ExpenseCategorySummaryDto(
//
//                        String.valueOf(row[0]),
//
//                        row[1] != null
//                                ? (BigDecimal) row[1]
//                                : BigDecimal.ZERO
//
//                ))
//                .toList();
//    }

    @Override
    public List<ExpenseCategorySummaryDto> getExpenseCategorySummary() {

        return expenseRepo
                .getCategoryWiseExpense()
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


}
