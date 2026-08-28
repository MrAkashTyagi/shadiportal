package com.bigsquare.ShadiPortal.helper;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GuestHelper {

    // 1. HEADERS ARRAY: Sequence matching your excel layout precisely
    public static String[] HEADERS = {
            "guest_id",       // Index 0
            "email",          // Index 1
            "name",           // Index 2
            "gender",         // Index 3
            "guestCategory",  // Index 4
            "adultOrChild",   // Index 5
            "phoneNumber",    // Index 6
            "whatsapp_Number",// Index 7
            "familyId",       // Index 8
            "userId"          // Index 9
    };

    public static String SHEET_NAME = "GUESTS_DETAILS";

    // DATA TO EXCEL DUMP UTILITY
    public static ByteArrayInputStream dataToExcel(List<Guest> guestList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            Row row = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (Guest guest : guestList) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(guest.getId() != null ? guest.getId() : 0);
                dataRow.createCell(1).setCellValue(guest.getEmail() != null ? guest.getEmail() : "");
                dataRow.createCell(2).setCellValue(guest.getName() != null ? guest.getName() : "");
                dataRow.createCell(3).setCellValue(guest.getGender() != null ? guest.getGender() : "");
                dataRow.createCell(4).setCellValue(guest.getGuestCategory() != null ? guest.getGuestCategory() : "");
                dataRow.createCell(5).setCellValue(guest.getAdultOrchild() != null ? guest.getAdultOrchild() : "Adult");
                dataRow.createCell(6).setCellValue(guest.getPhoneNumber() != null ? guest.getPhoneNumber() : "");
                dataRow.createCell(7).setCellValue(guest.getWhatsapp_Number() != null ? guest.getWhatsapp_Number() : "");

                if (guest.getFamily() != null) {
                    dataRow.createCell(8).setCellValue(guest.getFamily().getId());
                } else {
                    dataRow.createCell(8).setCellValue("");
                }

                dataRow.createCell(9).setCellValue("");
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            workbook.close();
            out.close();
        }
    }

    public static boolean checkExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
//        return contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                .equals(contentType)
                || "application/octet-stream".equals(contentType);
    }

    // CONVERT EXCEL TO LIST OF GUESTS (100% PRO COMPILER COMPATIBLE VALUE SEQUENCE)
    public static List<Guest> convertExcelToListOfGuests(InputStream is) {
        List<Guest> guests = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();

            // FIX FIXED: Loop standard structure restored without typo texts interruptions
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Guest guest = new Guest();
                guest.setId(null);

                // Index 1: email
                Cell cell1 = row.getCell(0);
                guest.setEmail(dataFormatter.formatCellValue(cell1));

                // Index 2: name
                Cell cell2 = row.getCell(1);
                guest.setName(dataFormatter.formatCellValue(cell2));

                // Index 3: gender
                Cell cell3 = row.getCell(2);
                guest.setGender(dataFormatter.formatCellValue(cell3));

                // Index 4: guestCategory
                Cell cell4 = row.getCell(3);
                guest.setGuestCategory(dataFormatter.formatCellValue(cell4));

                // Index 5: adultOrChild ("Ad" mapped to "Adult", "Ch" mapped to "Child")
                Cell cell5 = row.getCell(4);
                String type = dataFormatter.formatCellValue(cell5).trim();
                if (type.equalsIgnoreCase("Ad")) {
                    guest.setAdultOrchild("Adult");
                } else if (type.equalsIgnoreCase("Ch")) {
                    guest.setAdultOrchild("Child");
                } else {
                    guest.setAdultOrchild(type.isEmpty() ? "Adult" : type);
                }

                // Index 6: phoneNumber
                Cell cell6 = row.getCell(5);
                guest.setPhoneNumber(dataFormatter.formatCellValue(cell6));

                // Index 7: whatsapp_Number
                Cell cell7 = row.getCell(6);
                String whatsapp = dataFormatter.formatCellValue(cell7).trim();
                guest.setWhatsapp_Number(whatsapp.isEmpty() ? guest.getPhoneNumber() : whatsapp);

                // Index 8: familyId relational foreign key configuration binding
                Cell cell8 = row.getCell(7);
                if (cell8 != null) {
                    try {
                        Integer familyIdPointer = null;
                        if (cell8.getCellType() == CellType.NUMERIC) {
                            familyIdPointer = (int) cell8.getNumericCellValue();
                        } else {
                            String rawIdText = dataFormatter.formatCellValue(cell8).trim();
                            if (!rawIdText.isEmpty()) {
                                familyIdPointer = Integer.parseInt(rawIdText);
                            }
                        }

                        if (familyIdPointer != null) {
                            Family linkedFamilyEntity = new Family();
                            linkedFamilyEntity.setId(familyIdPointer);
                            guest.setFamily(linkedFamilyEntity);
                        }
                    } catch (Exception ex) {
                        System.err.println("Error mapping relational index " + i + ": " + ex.getMessage());
                    }
                }

                // Index 9 (userId) processing elements omitted dynamically

                if (guest.getName() != null && !guest.getName().trim().isEmpty()) {
                    guests.add(guest);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return guests;
    }
}
