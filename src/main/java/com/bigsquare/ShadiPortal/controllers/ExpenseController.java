package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.ExpenseCategorySummaryDto;
import com.bigsquare.ShadiPortal.dto.ExpenseSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@CrossOrigin("*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

//    // Create Expense
//    @PostMapping(
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public Expense createExpense(
//            @RequestPart("expense") Expense expense,
//            @RequestPart(
//                    value = "bill",
//                    required = false
//            ) MultipartFile bill
//    ) {
//        return expenseService.createExpense(
//                expense,
//                bill
//        );
//    }
//
//    @PostMapping(
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public String createExpense(
//            @RequestPart("expense") String expense,
//            @RequestPart(
//                    value = "bill",
//                    required = false
//            ) MultipartFile bill
//    ) {
//
//        System.out.println(expense);
//
//        return "Success";
//    }


    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Expense createExpense(
            @RequestPart("expense") String expenseJson,
            @RequestPart(
                    value = "bill",
                    required = false
            ) MultipartFile bill
    ) {

        try {

//            ObjectMapper mapper = new ObjectMapper();

            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            Expense expense =
                    new ObjectMapper()
                            .findAndRegisterModules()
                            .readValue(
                                    expenseJson,
                                    Expense.class
                            );

            return expenseService.createExpense(
                    expense,
                    bill
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error while creating expense",
                    e
            );

        }
    }

    @GetMapping("/bill/{id}")
    public ResponseEntity<Resource> viewBill(
            @PathVariable Integer id
    ) throws Exception {

        Expense expense =
                expenseService.getExpenseById(id);

        Path filePath =
                Paths.get(expense.getBillPath());

        Resource resource =
                new UrlResource(filePath.toUri());

        String contentType =
                Files.probeContentType(filePath);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType != null
                                        ? contentType
                                        : MediaType.APPLICATION_OCTET_STREAM_VALUE
                        )
                )
                .body(resource);
    }

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
                    value = "bill",
                    required = false
            ) MultipartFile bill
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
                    bill
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
    public ResponseEntity<byte[]> exportExpenses() {

        byte[] excelData = expenseService.exportExpenses();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=expenses.xlsx"
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(excelData);
    }

    @GetMapping("/bill/download/{id}")
    public ResponseEntity<Resource> downloadBill(
            @PathVariable Integer id
    ) throws Exception {

        Expense expense =
                expenseService.getExpenseById(id);

        Path filePath =
                Paths.get(expense.getBillPath());

        Resource resource =
                new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" +
                                filePath.getFileName().toString()
                )
                .body(resource);
    }

    @GetMapping("/summary")
    public ExpenseSummaryDto getExpenseSummary() {
        return expenseService.getExpenseSummary();
    }

    @GetMapping("/category-summary")
    public List<ExpenseCategorySummaryDto>
    getCategorySummary() {

        return expenseService
                .getExpenseCategorySummary();
    }
}
