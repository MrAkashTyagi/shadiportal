package com.bigsquare.ShadiPortal.helper;

import com.bigsquare.ShadiPortal.entities.Expense;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpenseHelper {

    public static final String[] HEADERS = {
            "expenseName",
            "category",
            "description",
            "totalAmount",
            "paidAmount",
            "expenseDate",
            "paidBy"
    };

    public static final String SHEET_NAME =
            "EXPENSE_DETAILS";

    public static boolean checkExcelFormat(
            MultipartFile file
    ) {

        String type =
                file.getContentType();

        return
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        .equals(type)
                        ||
                        "application/octet-stream"
                                .equals(type);
    }

    public static ByteArrayInputStream dataToExcel(
            List<Expense> expenses
    ) throws IOException {

        Workbook workbook =
                new XSSFWorkbook();

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        Sheet sheet =
                workbook.createSheet(
                        SHEET_NAME
                );

        Row header =
                sheet.createRow(0);

        for (int i = 0;
             i < HEADERS.length;
             i++) {

            header.createCell(i)
                    .setCellValue(
                            HEADERS[i]
                    );
        }

        int rowIndex = 1;

        for (Expense expense : expenses) {

            Row row =
                    sheet.createRow(
                            rowIndex++
                    );

            row.createCell(0)
                    .setCellValue(
                            expense.getExpenseName()
                    );

            row.createCell(1)
                    .setCellValue(
                            expense.getCategory()
                    );

            row.createCell(2)
                    .setCellValue(
                            expense.getDescription()
                    );

            row.createCell(3)
                    .setCellValue(
                            expense.getTotalAmount()
                    );

            row.createCell(4)
                    .setCellValue(
                            expense.getPaidAmount()
                    );

            row.createCell(5)
                    .setCellValue(
                            expense.getExpenseDate() != null
                                    ? expense.getExpenseDate().toString()
                                    : ""
                    );

            row.createCell(6)
                    .setCellValue(
                            expense.getPaidBy()
                    );
        }

        workbook.write(out);

        workbook.close();

        return new ByteArrayInputStream(
                out.toByteArray()
        );
    }

    public static List<Expense>
    convertExcelToListOfExpenses(
            InputStream inputStream
    ) {

        List<Expense> expenses =
                new ArrayList<>();

        DataFormatter formatter =
                new DataFormatter();

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook(inputStream)
        ) {

            Sheet sheet =
                    workbook.getSheetAt(0);

            for (
                    int i = 1;
                    i <= sheet.getLastRowNum();
                    i++
            ) {

                Row row =
                        sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                Expense expense =
                        new Expense();

                expense.setExpenseName(
                        formatter.formatCellValue(
                                row.getCell(0)
                        )
                );

                expense.setCategory(
                        formatter.formatCellValue(
                                row.getCell(1)
                        )
                );

                expense.setDescription(
                        formatter.formatCellValue(
                                row.getCell(2)
                        )
                );

                String total =
                        formatter.formatCellValue(
                                row.getCell(3)
                        );

                String paid =
                        formatter.formatCellValue(
                                row.getCell(4)
                        );

                expense.setTotalAmount(
                        total.isBlank()
                                ? 0.0
                                : Double.parseDouble(
                                total.replace(",", "")
                        )
                );

                expense.setPaidAmount(
                        paid.isBlank()
                                ? 0.0
                                : Double.parseDouble(
                                paid.replace(",", "")
                        )
                );

                Cell dateCell = row.getCell(5);

                if (dateCell != null) {

                    try {

                        if (
                                dateCell.getCellType()
                                        == CellType.NUMERIC
                                        && DateUtil.isCellDateFormatted(
                                        dateCell
                                )
                        ) {

                            expense.setExpenseDate(
                                    dateCell
                                            .getLocalDateTimeCellValue()
                                            .toLocalDate()
                            );

                        } else {

                            String date =
                                    formatter.formatCellValue(
                                            dateCell
                                    );

                            if (!date.isBlank()) {

                                DateTimeFormatter formatter1 =
                                        DateTimeFormatter.ofPattern(
                                                "M/d/yy"
                                        );

                                expense.setExpenseDate(
                                        LocalDate.parse(
                                                date,
                                                formatter1
                                        )
                                );
                            }
                        }

                    } catch (Exception exception) {

                        System.out.println(
                                "Date parse error: "
                                        + exception.getMessage()
                        );
                    }
                }

                expense.setPaidBy(
                        formatter.formatCellValue(
                                row.getCell(6)
                        )
                );

                expenses.add(expense);
            }

        } catch (Exception exception) {

            exception.printStackTrace();
        }

        return expenses;
    }
}
