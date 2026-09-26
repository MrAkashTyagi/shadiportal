package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.ExpenseCategorySummaryDto;
import com.bigsquare.ShadiPortal.dto.ExpenseSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.ExpenseBill;
import com.bigsquare.ShadiPortal.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@CrossOrigin("*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Expense createExpense(
            @RequestPart("expense") String expenseJson,
            @RequestPart(
                    value = "bills",
                    required = false
            ) MultipartFile[] bills
    ) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            mapper.findAndRegisterModules();

            Expense expense =
                    mapper.readValue(
                            expenseJson,
                            Expense.class
                    );

            return expenseService.createExpense(
                    expense,
                    bills
            );

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Error while creating expense",
                    exception
            );
        }
    }


//    @GetMapping("/{expenseId}/bills")
//    public ResponseEntity<byte[]> viewBill(
//            @PathVariable Integer id
//    ) {
//
//        Expense expense =
//                expenseService
//                        .getExpenseById(id);
//
//        byte[] billData =
//                fetchCloudinaryBill(
//                        expense
//                );
//
//        return ResponseEntity
//                .ok()
//                .contentType(
//                        resolveBillContentType(
//                                expense
//                        )
//                )
//                .contentLength(
//                        billData.length
//                )
//                .body(
//                        billData
//                );
//    }
    // Update Expense
//    @PutMapping("/{id}")
//    public Expense updateExpense(
//            @PathVariable Integer id,
//            @RequestBody Expense expense
//    ) {
//        return expenseService.updateExpense(id, expense);
//    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Expense updateExpense(
            @PathVariable Integer id,
            @RequestPart("expense") String expenseJson,
            @RequestPart(
                    value = "bills",
                    required = false
            ) MultipartFile[] bills
    ) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            Expense expense =
                    mapper.readValue(
                            expenseJson,
                            Expense.class
                    );

            return expenseService.updateExpense(
                    id,
                    expense,
                    bills
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error updating expense",
                    e
            );

        }

    }


    // Get All Expenses (dropdown/report use)
    @GetMapping("/all")
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    // Get Expense By Id
    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Integer id) {
        return expenseService.getExpenseById(id);
    }

    // Paginated Expenses
    @GetMapping
    public Page<Expense> getPaginatedExpenses(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String category
    ) {

        return expenseService.getPaginatedExpenses(

                page,
                size,
                search,
                category
        );
    }

    // Delete Expense
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Integer id) {
        expenseService.deleteExpense(id);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExpenses(

            @RequestParam(
                    defaultValue = ""
            ) String search,

            @RequestParam(
                    defaultValue = ""
            ) String category

    ) {

        byte[] excelData =
                expenseService.exportExpenses(
                        search,
                        category
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"expenses.xlsx\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excelData);
    }


    @GetMapping("/bill/download/{id}")
    public ResponseEntity<byte[]> downloadBill(
            @PathVariable Integer id
    ) {

        Expense expense =
                expenseService
                        .getExpenseById(id);

        byte[] billData =
                fetchCloudinaryBill(
                        expense
                );

        String fileName =
                resolveBillFileName(
                        expense
                );

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )
                .contentType(
                        resolveBillContentType(
                                expense
                        )
                )
                .contentLength(
                        billData.length
                )
                .body(
                        billData
                );
    }

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryDto>
    getExpenseSummary() {

        return ResponseEntity.ok(
                expenseService.getExpenseSummary(

                )
        );
    }

    @GetMapping("/category-summary")
    public List<ExpenseCategorySummaryDto>
    getCategorySummary() {

        return expenseService
                .getExpenseCategorySummary();
    }

    private byte[] fetchCloudinaryBill(
            Expense expense
    ) {

        if (
                expense.getBillUrl() == null
                        || expense.getBillUrl().isBlank()
        ) {

            throw new IllegalStateException(
                    "Bill URL is not available"
            );
        }

        try (
                InputStream inputStream =
                        URI.create(
                                        expense.getBillUrl()
                                )
                                .toURL()
                                .openStream();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            inputStream.transferTo(
                    outputStream
            );

            return outputStream.toByteArray();

        } catch (Exception exception) {

            // 👇 Ye add kar
            exception.printStackTrace();

            throw new RuntimeException(
                    "Cloudinary bill fetch failed",
                    exception
            );
        }
    }
    private MediaType resolveBillContentType(
            Expense expense
    ) {

        String contentType =
                expense.getBillContentType();

        if (
                contentType == null
                        || contentType.isBlank()
        ) {

            return MediaType
                    .APPLICATION_OCTET_STREAM;
        }

        try {

            return MediaType.parseMediaType(
                    contentType
            );

        } catch (Exception exception) {

            return MediaType
                    .APPLICATION_OCTET_STREAM;
        }
    }

    private String resolveBillFileName(
            Expense expense
    ) {

        String originalName =
                expense.getBillOriginalName();

        if (
                originalName == null
                        || originalName.isBlank()
        ) {

            return "bill";
        }

        return originalName
                .replace(
                        "\"",
                        ""
                )
                .replace(
                        "\r",
                        ""
                )
                .replace(
                        "\n",
                        ""
                );
    }

    @GetMapping("/{expenseId}/bills")
    public List<ExpenseBill> getExpenseBills(
            @PathVariable Integer expenseId
    ) {

        Expense expense =
                expenseService.getExpenseById(
                        expenseId
                );

        return expense.getBills();
    }
}
