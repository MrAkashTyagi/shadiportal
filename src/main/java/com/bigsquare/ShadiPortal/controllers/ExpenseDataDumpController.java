package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.helper.ExpenseHelper;
import com.bigsquare.ShadiPortal.serviceImpl.ExpenseDataDumpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/expense-dump")
@CrossOrigin("*")
public class ExpenseDataDumpController {

    @Autowired
    private ExpenseDataDumpServiceImpl expenseDataDumpService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Integer userId
    ) {

        if (ExpenseHelper.checkExcelFormat(file)) {

            expenseDataDumpService.save(
                    file,
                    userId
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Expense dump imported successfully"
                    )
            );
        }

        return ResponseEntity
                .badRequest()
                .body("Please upload excel file only");
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadExcel(
            @RequestParam Integer userId
    ) throws IOException {

        ByteArrayInputStream stream =
                expenseDataDumpService.getActualData(
                        userId
                );

        InputStreamResource file =
                new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=expenses.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
}
