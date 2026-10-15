package com.bigsquare.ShadiPortal.helper;

import com.bigsquare.ShadiPortal.entities.WeddingContact;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class WeddingContactHelper {

    public static final String[] HEADERS = {
            "name",
            "category",
            "contactPerson",
            "mobileNumber",
            "alternateNumber",
            "whatsappNumber",
            "address",
            "mapLink",
            "notes",
            "advancePaid",
            "pendingAmount"
    };

    public static final String SHEET_NAME =
            "WEDDING_CONTACTS";

    private WeddingContactHelper() {
    }

    public static boolean checkExcelFormat(
            MultipartFile file
    ) {

        if (
                file == null
                        || file.isEmpty()
        ) {
            return false;
        }

        String fileName =
                file.getOriginalFilename();

        boolean validExtension =
                fileName != null
                        && fileName
                        .toLowerCase()
                        .endsWith(".xlsx");

        String contentType =
                file.getContentType();

        boolean validContentType =
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        .equals(contentType)
                        || "application/octet-stream"
                        .equals(contentType);

        return validExtension
                && validContentType;
    }

    public static ByteArrayInputStream dataToExcel(
            List<WeddingContact> contacts
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

            createHeaderRow(sheet);

            int rowIndex = 1;

            for (WeddingContact contact : contacts) {

                Row row =
                        sheet.createRow(
                                rowIndex++
                        );

                setStringCell(
                        row,
                        0,
                        contact.getName()
                );

                setStringCell(
                        row,
                        1,
                        contact.getCategory()
                );

                setStringCell(
                        row,
                        2,
                        contact.getContactPerson()
                );

                setStringCell(
                        row,
                        3,
                        contact.getMobileNumber()
                );

                setStringCell(
                        row,
                        4,
                        contact.getAlternateNumber()
                );

                setStringCell(
                        row,
                        5,
                        contact.getWhatsappNumber()
                );

                setStringCell(
                        row,
                        6,
                        contact.getAddress()
                );

                setStringCell(
                        row,
                        7,
                        contact.getMapLink()
                );

                setStringCell(
                        row,
                        8,
                        contact.getNotes()
                );

                setNumberCell(
                        row,
                        9,
                        contact.getAdvancePaid()
                );

                setNumberCell(
                        row,
                        10,
                        contact.getPendingAmount()
                );
            }

            for (
                    int columnIndex = 0;
                    columnIndex < HEADERS.length;
                    columnIndex++
            ) {

                sheet.autoSizeColumn(
                        columnIndex
                );
            }

            workbook.write(
                    outputStream
            );

            return new ByteArrayInputStream(
                    outputStream.toByteArray()
            );
        }
    }

    public static List<WeddingContact>
    convertExcelToListOfContacts(
            InputStream inputStream
    ) {

        List<WeddingContact> contacts =
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

                String name =
                        getCellText(
                                row,
                                0,
                                formatter
                        );

                if (
                        name == null
                                || name.isBlank()
                ) {

                    throw new IllegalArgumentException(
                            "Name is required at Excel row "
                                    + (rowIndex + 1)
                    );
                }

                WeddingContact contact =
                        WeddingContact.builder()
                                .name(name.trim())
                                .category(
                                        getNullableCellText(
                                                row,
                                                1,
                                                formatter
                                        )
                                )
                                .contactPerson(
                                        getNullableCellText(
                                                row,
                                                2,
                                                formatter
                                        )
                                )
                                .mobileNumber(
                                        getNullableCellText(
                                                row,
                                                3,
                                                formatter
                                        )
                                )
                                .alternateNumber(
                                        getNullableCellText(
                                                row,
                                                4,
                                                formatter
                                        )
                                )
                                .whatsappNumber(
                                        getNullableCellText(
                                                row,
                                                5,
                                                formatter
                                        )
                                )
                                .address(
                                        getNullableCellText(
                                                row,
                                                6,
                                                formatter
                                        )
                                )
                                .mapLink(
                                        getNullableCellText(
                                                row,
                                                7,
                                                formatter
                                        )
                                )
                                .notes(
                                        getNullableCellText(
                                                row,
                                                8,
                                                formatter
                                        )
                                )
                                .advancePaid(
                                        parseAmount(
                                                row,
                                                9,
                                                formatter,
                                                "advancePaid",
                                                rowIndex
                                        )
                                )
                                .pendingAmount(
                                        parseAmount(
                                                row,
                                                10,
                                                formatter,
                                                "pendingAmount",
                                                rowIndex
                                        )
                                )
                                .build();

                contacts.add(contact);
            }

        } catch (IllegalArgumentException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Unable to read wedding contacts Excel file",
                    exception
            );
        }

        return contacts;
    }

    private static void createHeaderRow(
            Sheet sheet
    ) {

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
                    actualHeader == null
                            || !HEADERS[index]
                            .equalsIgnoreCase(
                                    actualHeader.trim()
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

    private static boolean isEmptyRow(
            Row row,
            DataFormatter formatter
    ) {

        for (
                int index = 0;
                index < HEADERS.length;
                index++
        ) {

            String value =
                    getCellText(
                            row,
                            index,
                            formatter
                    );

            if (
                    value != null
                            && !value.isBlank()
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

        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }

        return value.trim();
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

    private static Double parseAmount(
            Row row,
            int columnIndex,
            DataFormatter formatter,
            String fieldName,
            int rowIndex
    ) {

        String value =
                getCellText(
                        row,
                        columnIndex,
                        formatter
                );

        if (
                value == null
                        || value.isBlank()
        ) {
            return 0.0;
        }

        try {

            double amount =
                    Double.parseDouble(
                            value
                                    .replace(",", "")
                                    .replace("₹", "")
                                    .trim()
                    );

            if (amount < 0) {

                throw new IllegalArgumentException(
                        fieldName
                                + " cannot be negative at Excel row "
                                + (rowIndex + 1)
                );
            }

            return amount;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Invalid "
                            + fieldName
                            + " at Excel row "
                            + (rowIndex + 1)
                            + ": "
                            + value
            );
        }
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

    private static void setNumberCell(
            Row row,
            int columnIndex,
            Double value
    ) {

        row.createCell(columnIndex)
                .setCellValue(
                        value != null
                                ? value
                                : 0.0
                );
    }
}
