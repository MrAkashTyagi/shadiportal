package com.bigsquare.ShadiPortal.helper;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
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

public class GuestHelper {

    /*
     * Final clean Guest import/export format.
     *
     * Removed:
     * guest_id
     * familyId
     * userId
     */
    public static final String[] HEADERS = {
            "email",
            "name",
            "gender",
            "guestCategory",
            "adultOrChild",
            "phoneNumber",
            "whatsapp_Number",
            "gift",
            "cash",
            "stay",
            "family",
            "invitationSent"
    };

    public static final String SHEET_NAME =
            "GUESTS_DETAILS";

    /*
     * Converts Guest data into Excel.
     */
    public static ByteArrayInputStream dataToExcel(
            List<Guest> guestList
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
                    int headerIndex = 0;
                    headerIndex < HEADERS.length;
                    headerIndex++
            ) {

                headerRow.createCell(
                        headerIndex
                ).setCellValue(
                        HEADERS[headerIndex]
                );
            }

            int rowIndex = 1;

            for (Guest guest : guestList) {

                Row dataRow =
                        sheet.createRow(
                                rowIndex++
                        );

                dataRow.createCell(0)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getEmail()
                                )
                        );

                dataRow.createCell(1)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getName()
                                )
                        );

                dataRow.createCell(2)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getGender()
                                )
                        );

                dataRow.createCell(3)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getGuestCategory()
                                )
                        );

                dataRow.createCell(4)
                        .setCellValue(
                                guest.getAdultOrchild() != null
                                        && !guest.getAdultOrchild().isBlank()
                                        ? guest.getAdultOrchild()
                                        : "Adult"
                        );

                dataRow.createCell(5)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getPhoneNumber()
                                )
                        );

                dataRow.createCell(6)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getWhatsapp_Number()
                                )
                        );

                dataRow.createCell(7)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getGift()
                                )
                        );

                dataRow.createCell(8)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getCash()
                                )
                        );

                dataRow.createCell(9)
                        .setCellValue(
                                valueOrEmpty(
                                        guest.getStay()
                                )
                        );

                dataRow.createCell(10)
                        .setCellValue(
                                guest.getFamily() != null
                                        ? valueOrEmpty(
                                        guest.getFamily()
                                                .getFamilyName()
                                )
                                        : ""
                        );

                dataRow.createCell(11)
                        .setCellValue(
                                Boolean.TRUE.equals(
                                        guest.getInvitationSent()
                                )
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

    /*
     * Checks supported Excel formats.
     */
    public static boolean checkExcelFormat(
            MultipartFile file
    ) {

        if (
                file == null ||
                        file.isEmpty()
        ) {
            return false;
        }

        String contentType =
                file.getContentType();

        String originalFileName =
                file.getOriginalFilename();

        boolean validContentType =
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        .equals(contentType)
                        ||
                        "application/octet-stream"
                                .equals(contentType);

        boolean validExtension =
                originalFileName != null
                        &&
                        originalFileName
                                .toLowerCase()
                                .endsWith(".xlsx");

        return validContentType
                || validExtension;
    }

    /*
     * Converts Excel records into Guest objects.
     *
     * User ownership is intentionally not read from Excel.
     * GuestDataDumpServiceImpl assigns the current User.
     *
     * Family ID is also not read.
     * Family is resolved by family name for the current User.
     */
    public static List<Guest>
    convertExcelToListOfGuests(
            InputStream inputStream
    ) {

        List<Guest> guests =
                new ArrayList<>();

        DataFormatter formatter =
                new DataFormatter();

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook(
                                inputStream
                        )
        ) {

            Sheet sheet =
                    workbook.getSheetAt(0);

            for (
                    int rowIndex = 1;
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++
            ) {

                Row row =
                        sheet.getRow(
                                rowIndex
                        );

                if (row == null) {
                    continue;
                }

                String email =
                        getCellValue(
                                row,
                                0,
                                formatter
                        );

                String name =
                        getCellValue(
                                row,
                                1,
                                formatter
                        );

                /*
                 * Completely empty records are ignored.
                 */
                if (
                        email.isBlank() &&
                                name.isBlank()
                ) {
                    continue;
                }

                Guest guest =
                        new Guest();

                /*
                 * ID will be generated by the database.
                 */
                guest.setId(null);

                guest.setEmail(
                        email
                );

                guest.setName(
                        name
                );

                guest.setGender(
                        getCellValue(
                                row,
                                2,
                                formatter
                        )
                );

                guest.setGuestCategory(
                        getCellValue(
                                row,
                                3,
                                formatter
                        )
                );

                String adultOrChild =
                        getCellValue(
                                row,
                                4,
                                formatter
                        );

                if (
                        adultOrChild.equalsIgnoreCase(
                                "Ad"
                        )
                ) {

                    guest.setAdultOrchild(
                            "Adult"
                    );

                } else if (
                        adultOrChild.equalsIgnoreCase(
                                "Ch"
                        )
                ) {

                    guest.setAdultOrchild(
                            "Child"
                    );

                } else {

                    guest.setAdultOrchild(
                            adultOrChild.isBlank()
                                    ? "Adult"
                                    : adultOrChild
                    );
                }

                guest.setPhoneNumber(
                        getCellValue(
                                row,
                                5,
                                formatter
                        )
                );

                String whatsappNumber =
                        getCellValue(
                                row,
                                6,
                                formatter
                        );

                guest.setWhatsapp_Number(
                        whatsappNumber.isBlank()
                                ? guest.getPhoneNumber()
                                : whatsappNumber
                );

                guest.setGift(
                        getCellValue(
                                row,
                                7,
                                formatter
                        )
                );

                guest.setCash(
                        getCellValue(
                                row,
                                8,
                                formatter
                        )
                );

                guest.setStay(
                        getCellValue(
                                row,
                                9,
                                formatter
                        )
                );

                String familyName =
                        getCellValue(
                                row,
                                10,
                                formatter
                        );

                if (!familyName.isBlank()) {

                    Family family =
                            new Family();

                    family.setFamilyName(
                            familyName.trim()
                    );

                    guest.setFamily(
                            family
                    );
                }

                guest.setInvitationSent(
                        getBooleanCellValue(
                                row,
                                11,
                                formatter
                        )
                );

                guests.add(
                        guest
                );
            }

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Unable to read guest Excel file",
                    exception
            );
        }

        return guests;
    }

    private static String getCellValue(
            Row row,
            int cellIndex,
            DataFormatter formatter
    ) {

        Cell cell =
                row.getCell(
                        cellIndex,
                        Row.MissingCellPolicy
                                .RETURN_BLANK_AS_NULL
                );

        if (cell == null) {
            return "";
        }

        return formatter
                .formatCellValue(cell)
                .trim();
    }

    private static Boolean getBooleanCellValue(
            Row row,
            int cellIndex,
            DataFormatter formatter
    ) {

        String value =
                getCellValue(
                        row,
                        cellIndex,
                        formatter
                );

        if (value.isBlank()) {
            return false;
        }

        return value.equalsIgnoreCase(
                "true"
        )
                ||
                value.equalsIgnoreCase(
                        "yes"
                )
                ||
                value.equalsIgnoreCase(
                        "y"
                )
                ||
                value.equalsIgnoreCase(
                        "1"
                );
    }

    private static String valueOrEmpty(
            String value
    ) {

        return value != null
                ? value
                : "";
    }
}
