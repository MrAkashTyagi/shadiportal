package com.bigsquare.ShadiPortal.helper;

import com.bigsquare.ShadiPortal.entities.WeddingTask;
import com.bigsquare.ShadiPortal.enums.TaskPriority;
import com.bigsquare.ShadiPortal.enums.TaskStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class WeddingTaskHelper {

    public static final String[] HEADERS = {
            "title",
            "description",
            "status",
            "priority",
            "dueDate",
            "assignedTo"
    };

    public static final String SHEET_NAME =
            "WEDDING_TASKS";

    private WeddingTaskHelper() {
    }

    public static boolean checkExcelFormat(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            return false;
        }

        String fileName =
                file.getOriginalFilename();

        return fileName != null
                && fileName
                .toLowerCase()
                .endsWith(".xlsx");
    }

    public static ByteArrayInputStream dataToExcel(
            List<WeddingTask> tasks
    ) throws IOException {

        try (
                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet(
                            SHEET_NAME
                    );

            Row headerRow =
                    sheet.createRow(0);

            for (
                    int index = 0;
                    index < HEADERS.length;
                    index++
            ) {

                headerRow
                        .createCell(index)
                        .setCellValue(
                                HEADERS[index]
                        );
            }

            int rowIndex = 1;

            for (WeddingTask task : tasks) {

                Row row =
                        sheet.createRow(
                                rowIndex++
                        );

                setStringCell(
                        row,
                        0,
                        task.getTitle()
                );

                setStringCell(
                        row,
                        1,
                        task.getDescription()
                );

                setStringCell(
                        row,
                        2,
                        task.getStatus() != null
                                ? task.getStatus().name()
                                : TaskStatus.TODO.name()
                );

                setStringCell(
                        row,
                        3,
                        task.getPriority() != null
                                ? task.getPriority().name()
                                : TaskPriority.MEDIUM.name()
                );

                setStringCell(
                        row,
                        4,
                        task.getDueDate() != null
                                ? task.getDueDate().toString()
                                : ""
                );

                setStringCell(
                        row,
                        5,
                        task.getAssignedTo()
                );
            }

            for (
                    int index = 0;
                    index < HEADERS.length;
                    index++
            ) {

                sheet.autoSizeColumn(index);
            }

            workbook.write(outputStream);

            return new ByteArrayInputStream(
                    outputStream.toByteArray()
            );
        }
    }

    public static List<WeddingTask>
    convertExcelToListOfTasks(
            InputStream inputStream
    ) {

        List<WeddingTask> tasks =
                new ArrayList<>();

        DataFormatter formatter =
                new DataFormatter();

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook(inputStream)
        ) {

            Sheet sheet =
                    workbook.getSheetAt(0);

            validateHeaders(
                    sheet,
                    formatter
            );

            for (
                    int rowIndex = 1;
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++
            ) {

                Row row =
                        sheet.getRow(rowIndex);

                if (
                        row == null
                                || isEmptyRow(
                                row,
                                formatter
                        )
                ) {
                    continue;
                }

                String title =
                        getNullableCellText(
                                row,
                                0,
                                formatter
                        );

                if (title == null) {

                    throw new IllegalArgumentException(
                            "Task title is required at Excel row "
                                    + (rowIndex + 1)
                    );
                }

                WeddingTask task =
                        new WeddingTask();

                task.setTitle(title);

                task.setDescription(
                        getNullableCellText(
                                row,
                                1,
                                formatter
                        )
                );

                task.setStatus(
                        parseStatus(
                                getNullableCellText(
                                        row,
                                        2,
                                        formatter
                                ),
                                rowIndex
                        )
                );

                task.setPriority(
                        parsePriority(
                                getNullableCellText(
                                        row,
                                        3,
                                        formatter
                                ),
                                rowIndex
                        )
                );

                task.setDueDate(
                        parseDate(
                                row,
                                4,
                                formatter,
                                rowIndex
                        )
                );

                task.setAssignedTo(
                        getNullableCellText(
                                row,
                                5,
                                formatter
                        )
                );

                tasks.add(task);
            }

        } catch (IllegalArgumentException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Unable to read wedding tasks Excel file",
                    exception
            );
        }

        return tasks;
    }

    private static void validateHeaders(
            Sheet sheet,
            DataFormatter formatter
    ) {

        Row headerRow =
                sheet.getRow(0);

        if (headerRow == null) {

            throw new IllegalArgumentException(
                    "Excel header row is missing"
            );
        }

        for (
                int index = 0;
                index < HEADERS.length;
                index++
        ) {

            String actualHeader =
                    getCellText(
                            headerRow,
                            index,
                            formatter
                    );

            if (
                    !HEADERS[index]
                            .equalsIgnoreCase(
                                    actualHeader
                            )
            ) {

                throw new IllegalArgumentException(
                        "Invalid Excel header at column "
                                + (index + 1)
                                + ". Expected: "
                                + HEADERS[index]
                );
            }
        }
    }

    private static TaskStatus parseStatus(
            String value,
            int rowIndex
    ) {

        if (
                value == null
                        || value.isBlank()
        ) {
            return TaskStatus.TODO;
        }

        String normalizedValue =
                value.trim()
                        .toUpperCase()
                        .replace("-", "_")
                        .replace(" ", "_");

        /*
         * User-friendly Excel value "To Do"
         * enum value TODO ke saath map hogi.
         */
        if (
                normalizedValue.equals("TO_DO")
                        || normalizedValue.equals("TODO")
        ) {
            return TaskStatus.TODO;
        }

        if (
                normalizedValue.equals("IN_PROGRESS")
                        || normalizedValue.equals("INPROGRESS")
        ) {
            return TaskStatus.IN_PROGRESS;
        }

        if (
                normalizedValue.equals("DONE")
                        || normalizedValue.equals("COMPLETED")
                        || normalizedValue.equals("COMPLETE")
        ) {
            return TaskStatus.DONE;
        }

        throw new IllegalArgumentException(
                "Invalid status at Excel row "
                        + (rowIndex + 1)
                        + ": "
                        + value
                        + ". Allowed values: "
                        + "TODO, TO DO, IN_PROGRESS, "
                        + "IN PROGRESS, DONE"
        );
    }

    private static TaskPriority parsePriority(
            String value,
            int rowIndex
    ) {

        if (value == null) {
            return TaskPriority.MEDIUM;
        }

        try {

            return TaskPriority.valueOf(
                    value.trim()
                            .toUpperCase()
            );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid priority at Excel row "
                            + (rowIndex + 1)
                            + ". Allowed values: "
                            + "HIGH, MEDIUM, LOW"
            );
        }
    }

    private static LocalDate parseDate(
            Row row,
            int columnIndex,
            DataFormatter formatter,
            int rowIndex
    ) {

        Cell cell =
                row.getCell(
                        columnIndex,
                        Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
                );

        if (cell == null) {
            return null;
        }

        try {

            if (
                    cell.getCellType()
                            == CellType.NUMERIC
                            && DateUtil
                            .isCellDateFormatted(cell)
            ) {

                return cell
                        .getLocalDateTimeCellValue()
                        .toLocalDate();
            }

            String value =
                    formatter
                            .formatCellValue(cell)
                            .trim();

            if (value.isBlank()) {
                return null;
            }

            return LocalDate.parse(
                    value,
                    DateTimeFormatter.ISO_LOCAL_DATE
            );

        } catch (Exception exception) {

            throw new IllegalArgumentException(
                    "Invalid dueDate at Excel row "
                            + (rowIndex + 1)
                            + ". Expected format: yyyy-MM-dd"
            );
        }
    }

    private static boolean isEmptyRow(
            Row row,
            DataFormatter formatter
    ) {

        for (
                int index = 0;
                index < HEADERS.length;
                index++
        ) {

            if (
                    !getCellText(
                            row,
                            index,
                            formatter
                    ).isBlank()
            ) {
                return false;
            }
        }

        return true;
    }

    private static String getNullableCellText(
            Row row,
            int columnIndex,
            DataFormatter formatter
    ) {

        String value =
                getCellText(
                        row,
                        columnIndex,
                        formatter
                );

        return value.isBlank()
                ? null
                : value;
    }

    private static String getCellText(
            Row row,
            int columnIndex,
            DataFormatter formatter
    ) {

        Cell cell =
                row.getCell(
                        columnIndex,
                        Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
                );

        if (cell == null) {
            return "";
        }

        return formatter
                .formatCellValue(cell)
                .trim();
    }

    private static void setStringCell(
            Row row,
            int columnIndex,
            String value
    ) {

        row.createCell(columnIndex)
                .setCellValue(
                        value != null
                                ? value
                                : ""
                );
    }
}
