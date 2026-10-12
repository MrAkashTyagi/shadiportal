package com.bigsquare.ShadiPortal.helper;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.entities.StayAllocation;
import com.bigsquare.ShadiPortal.entities.StayLocation;
import com.bigsquare.ShadiPortal.entities.StayRoom;
import com.bigsquare.ShadiPortal.enums.StayAllocationStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StayAllocationExcelHelper {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final String[] ALLOCATION_HEADERS = {
            "Guest Name",
            "Family Name",
            "Location",
            "Location Type",
            "Room / Unit",
            "Floor",
            "Room Capacity",
            "Status",
            "Check-In Date",
            "Check-Out Date",
            "Notes"
    };

    private static final String[] ROOM_HEADERS = {
            "Location",
            "Location Type",
            "Room / Unit",
            "Floor",
            "Room Status",
            "Capacity",
            "Planned",
            "Checked In",
            "Occupied Beds",
            "Available Beds",
            "Occupancy %"
    };

    private static final String[] OCCUPANCY_HEADERS = {
            "Location",
            "Location Type",
            "Total Rooms / Units",
            "Total Capacity",
            "Planned Guests",
            "Checked-In Guests",
            "Occupied Beds",
            "Available Beds",
            "Occupancy %"
    };

    private StayAllocationExcelHelper() {
    }

    public static ByteArrayInputStream exportStayData(
            List<StayAllocation> allocations,
            List<StayRoom> rooms
    ) throws IOException {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle percentageStyle = createPercentageStyle(workbook);

            createGuestAllocationSheet(
                    workbook,
                    allocations,
                    headerStyle
            );

            createRoomSummarySheet(
                    workbook,
                    rooms,
                    allocations,
                    headerStyle,
                    percentageStyle
            );

            createOccupancySummarySheet(
                    workbook,
                    rooms,
                    allocations,
                    headerStyle,
                    percentageStyle
            );

            workbook.write(outputStream);

            return new ByteArrayInputStream(
                    outputStream.toByteArray()
            );
        }
    }

    private static void createGuestAllocationSheet(
            Workbook workbook,
            List<StayAllocation> allocations,
            CellStyle headerStyle
    ) {
        Sheet sheet = workbook.createSheet("Guest Allocations");

        createHeaderRow(
                sheet,
                ALLOCATION_HEADERS,
                headerStyle
        );

        int rowIndex = 1;

        for (StayAllocation allocation : allocations) {
            Row row = sheet.createRow(rowIndex++);

            Guest guest = allocation.getGuest();
            Family family = guest.getFamily();
            StayRoom room = allocation.getRoom();
            StayLocation location = room.getLocation();

            setText(row, 0, guest.getName());
            setText(
                    row,
                    1,
                    family == null
                            ? "No Family"
                            : family.getFamilyName()
            );
            setText(row, 2, location.getName());
            setText(
                    row,
                    3,
                    location.getType() == null
                            ? ""
                            : location.getType().name().replace('_', ' ')
            );
            setText(row, 4, room.getName());
            setText(row, 5, room.getFloor());
            setNumber(row, 6, room.getCapacity());
            setText(
                    row,
                    7,
                    allocation.getStatus() == null
                            ? ""
                            : allocation.getStatus().name().replace('_', ' ')
            );
            setText(
                    row,
                    8,
                    allocation.getCheckInDate() == null
                            ? ""
                            : allocation.getCheckInDate().format(DATE_FORMATTER)
            );
            setText(
                    row,
                    9,
                    allocation.getCheckOutDate() == null
                            ? ""
                            : allocation.getCheckOutDate().format(DATE_FORMATTER)
            );
            setText(row, 10, allocation.getNotes());
        }

        finishSheet(
                sheet,
                ALLOCATION_HEADERS.length
        );
    }

    private static void createRoomSummarySheet(
            Workbook workbook,
            List<StayRoom> rooms,
            List<StayAllocation> allocations,
            CellStyle headerStyle,
            CellStyle percentageStyle
    ) {
        Sheet sheet = workbook.createSheet("Room Summary");

        createHeaderRow(
                sheet,
                ROOM_HEADERS,
                headerStyle
        );

        int rowIndex = 1;

        for (StayRoom room : rooms) {
            Row row = sheet.createRow(rowIndex++);
            StayLocation location = room.getLocation();

            long planned = countByRoomAndStatus(
                    allocations,
                    room.getId(),
                    StayAllocationStatus.PLANNED
            );

            long checkedIn = countByRoomAndStatus(
                    allocations,
                    room.getId(),
                    StayAllocationStatus.CHECKED_IN
            );

            long occupied = planned + checkedIn;
            long capacity = room.getCapacity() == null ? 0 : room.getCapacity();
            long available = Math.max(capacity - occupied, 0);
            double occupancy = capacity == 0
                    ? 0
                    : (double) occupied / capacity;

            setText(row, 0, location.getName());
            setText(
                    row,
                    1,
                    location.getType() == null
                            ? ""
                            : location.getType().name().replace('_', ' ')
            );
            setText(row, 2, room.getName());
            setText(row, 3, room.getFloor());
            setText(
                    row,
                    4,
                    Boolean.TRUE.equals(room.getActive())
                            ? "ACTIVE"
                            : "INACTIVE"
            );
            setNumber(row, 5, capacity);
            setNumber(row, 6, planned);
            setNumber(row, 7, checkedIn);
            setNumber(row, 8, occupied);
            setNumber(row, 9, available);
            setPercentage(row, 10, occupancy, percentageStyle);
        }

        finishSheet(
                sheet,
                ROOM_HEADERS.length
        );
    }

    private static void createOccupancySummarySheet(
            Workbook workbook,
            List<StayRoom> rooms,
            List<StayAllocation> allocations,
            CellStyle headerStyle,
            CellStyle percentageStyle
    ) {
        Sheet sheet = workbook.createSheet("Occupancy Summary");

        createHeaderRow(
                sheet,
                OCCUPANCY_HEADERS,
                headerStyle
        );

        Map<Integer, LocationSummary> summaries = new LinkedHashMap<>();

        for (StayRoom room : rooms) {
            StayLocation location = room.getLocation();

            LocationSummary summary = summaries.computeIfAbsent(
                    location.getId(),
                    ignored -> new LocationSummary(location)
            );

            long planned = countByRoomAndStatus(
                    allocations,
                    room.getId(),
                    StayAllocationStatus.PLANNED
            );

            long checkedIn = countByRoomAndStatus(
                    allocations,
                    room.getId(),
                    StayAllocationStatus.CHECKED_IN
            );

            summary.totalRooms++;
            summary.totalCapacity += room.getCapacity() == null
                    ? 0
                    : room.getCapacity();
            summary.plannedGuests += planned;
            summary.checkedInGuests += checkedIn;
        }

        int rowIndex = 1;

        long grandRooms = 0;
        long grandCapacity = 0;
        long grandPlanned = 0;
        long grandCheckedIn = 0;

        for (LocationSummary summary : summaries.values()) {
            Row row = sheet.createRow(rowIndex++);

            long occupied = summary.plannedGuests + summary.checkedInGuests;
            long available = Math.max(summary.totalCapacity - occupied, 0);
            double occupancy = summary.totalCapacity == 0
                    ? 0
                    : (double) occupied / summary.totalCapacity;

            setText(row, 0, summary.location.getName());
            setText(
                    row,
                    1,
                    summary.location.getType() == null
                            ? ""
                            : summary.location.getType().name().replace('_', ' ')
            );
            setNumber(row, 2, summary.totalRooms);
            setNumber(row, 3, summary.totalCapacity);
            setNumber(row, 4, summary.plannedGuests);
            setNumber(row, 5, summary.checkedInGuests);
            setNumber(row, 6, occupied);
            setNumber(row, 7, available);
            setPercentage(row, 8, occupancy, percentageStyle);

            grandRooms += summary.totalRooms;
            grandCapacity += summary.totalCapacity;
            grandPlanned += summary.plannedGuests;
            grandCheckedIn += summary.checkedInGuests;
        }

        createGrandTotalRow(
                workbook,
                sheet,
                rowIndex,
                grandRooms,
                grandCapacity,
                grandPlanned,
                grandCheckedIn
        );

        finishSheet(
                sheet,
                OCCUPANCY_HEADERS.length
        );
    }

    private static void createGrandTotalRow(
            Workbook workbook,
            Sheet sheet,
            int rowIndex,
            long totalRooms,
            long totalCapacity,
            long totalPlanned,
            long totalCheckedIn
    ) {
        Row row = sheet.createRow(rowIndex);
        CellStyle totalStyle = createTotalStyle(workbook);
        CellStyle totalPercentageStyle = createTotalPercentageStyle(workbook);

        long totalOccupied = totalPlanned + totalCheckedIn;
        long totalAvailable = Math.max(totalCapacity - totalOccupied, 0);
        double totalOccupancy = totalCapacity == 0
                ? 0
                : (double) totalOccupied / totalCapacity;

        setText(row, 0, "GRAND TOTAL");
        setText(row, 1, "");
        setNumber(row, 2, totalRooms);
        setNumber(row, 3, totalCapacity);
        setNumber(row, 4, totalPlanned);
        setNumber(row, 5, totalCheckedIn);
        setNumber(row, 6, totalOccupied);
        setNumber(row, 7, totalAvailable);
        setPercentage(row, 8, totalOccupancy, totalPercentageStyle);

        for (int index = 0; index < 8; index++) {
            row.getCell(index).setCellStyle(totalStyle);
        }
    }

    private static long countByRoomAndStatus(
            List<StayAllocation> allocations,
            Integer roomId,
            StayAllocationStatus status
    ) {
        return allocations.stream()
                .filter(allocation -> allocation.getRoom() != null)
                .filter(allocation -> allocation.getRoom().getId() != null)
                .filter(allocation -> allocation.getRoom().getId().equals(roomId))
                .filter(allocation -> allocation.getStatus() == status)
                .count();
    }

    private static void createHeaderRow(
            Sheet sheet,
            String[] headers,
            CellStyle headerStyle
    ) {
        Row row = sheet.createRow(0);

        for (int index = 0; index < headers.length; index++) {
            Cell cell = row.createCell(index);
            cell.setCellValue(headers[index]);
            cell.setCellStyle(headerStyle);
        }
    }

    private static void setText(
            Row row,
            int columnIndex,
            String value
    ) {
        row.createCell(columnIndex)
                .setCellValue(value == null ? "" : value);
    }

    private static void setNumber(
            Row row,
            int columnIndex,
            Number value
    ) {
        row.createCell(columnIndex)
                .setCellValue(value == null ? 0 : value.doubleValue());
    }

    private static void setPercentage(
            Row row,
            int columnIndex,
            double value,
            CellStyle style
    ) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void finishSheet(
            Sheet sheet,
            int totalColumns
    ) {
        sheet.createFreezePane(0, 1);

        for (int index = 0; index < totalColumns; index++) {
            sheet.autoSizeColumn(index);

            int currentWidth = sheet.getColumnWidth(index);
            int paddedWidth = currentWidth + 700;
            int maximumWidth = 40 * 256;

            sheet.setColumnWidth(
                    index,
                    Math.min(paddedWidth, maximumWidth)
            );
        }
    }

    private static CellStyle createHeaderStyle(
            Workbook workbook
    ) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    private static CellStyle createPercentageStyle(
            Workbook workbook
    ) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();

        style.setDataFormat(
                format.getFormat("0.00%")
        );

        return style;
    }

    private static CellStyle createTotalStyle(
            Workbook workbook
    ) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    private static CellStyle createTotalPercentageStyle(
            Workbook workbook
    ) {
        CellStyle style = createTotalStyle(workbook);
        DataFormat format = workbook.createDataFormat();

        style.setDataFormat(
                format.getFormat("0.00%")
        );

        return style;
    }

    private static class LocationSummary {

        private final StayLocation location;
        private long totalRooms;
        private long totalCapacity;
        private long plannedGuests;
        private long checkedInGuests;

        private LocationSummary(
                StayLocation location
        ) {
            this.location = location;
        }
    }
}
