package com.example;
// Import required classes for working with Excel files and colors
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color; // For working with RGB colors
import java.io.FileOutputStream; // To save the Excel file
import java.io.IOException; // To handle input-output exceptions
import java.util.*; // For using lists, maps, and other utilities
public class TimetableGenerator {
    // Lists for semester 7 software sections and courses
    private static final List<String> seSections7 = Arrays.asList("7AS", "7BS", "7CS", "7DS", "7ES");
    private static final List<String> seCourses7 = Arrays.asList("FY-1", "PPIT", "SE", "RS", "MM");
    // Lists for normal sections and courses of different semesters
    private static final List<String> sections7 = Arrays.asList("7A", "7B", "7C", "7D", "7E");
    private static final List<String> courses7 = Arrays.asList("FYP-1", "IR", "PSYC", "DLP", "FSPM");
    private static final List<String> sections5 = Arrays.asList("5A", "5B", "5C", "5D", "5E");
    private static final List<String> courses5 = Arrays.asList("PDC", "DB", "ALGO", "GT", "SDA");
    private static final List<String> sections3 = Arrays.asList("3A", "3B", "3C", "3D", "3E");
    private static final List<String> courses3 = Arrays.asList("COAL", "DSA", "DS", "LA", "POE");
    private static final List<String> sections1 = Arrays.asList("1A", "1B", "1C", "1D", "1E");
    private static final List<String> courses1 = Arrays.asList("PF", "ICT", "IRS", "CAL", "PS", "AP");
    // List of available rooms for the timetable
    private static final List<String> rooms = Arrays.asList("A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", 
                                                            "E1", "E2", "E3", "E4", "E5", "E6", "R11", "R12", "R109");
    // List of available timeslots for scheduling
    private static final List<String> timeslots = Arrays.asList("8-9", "9-10", "10-11", "11-12", "12-1", "1-2", "2-3", "3-4");
    // List of weekdays for the timetable
    private static final List<String> weekDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
    // Colors for different semesters
    private static final Map<String, String> semesterColors = new HashMap<>();
    static {
        semesterColors.put("7", "#FFFF00");  // Yellow for semester 7
        semesterColors.put("5", "#FF00FF");  // Magenta for semester 5
        semesterColors.put("3", "#FFCCCB");  // Light pink for semester 3
        semesterColors.put("1", "#CCE5FF");  // Light blue for semester 1
    }
    // Main method to generate the timetable
    public void generateTimetable() throws IOException {
        // Combine sections and courses for all semesters
        List<String[]> sectionsCourses = new ArrayList<>();
        sectionsCourses.addAll(generateCombinations(sections1, courses1));
        sectionsCourses.addAll(generateCombinations(sections3, courses3));
        sectionsCourses.addAll(generateCombinations(sections5, courses5));
        sectionsCourses.addAll(generateCombinations(sections7, courses7));
        sectionsCourses.addAll(generateCombinations(seSections7, seCourses7));
        // Create a new Excel workbook
        Workbook wb = new XSSFWorkbook();
        // Create a sheet for each weekday
        for (String day : weekDays) {
            Sheet sheet = wb.createSheet(day);  // Create a sheet
            createSheetHeader(sheet);  // Add headers to the sheet
            // Generate valid assignments of sections to rooms and timeslots
            Map<String, String> validMatching = generateValidMatching(sectionsCourses, rooms, timeslots);
            // Fill the timetable in the sheet
            fillTimetable(sheet, validMatching);
        }
        // Save the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream("Weekly_Timetable.xlsx")) {
            wb.write(fileOut);  // Write the workbook data
        }
        wb.close();  // Close the workbook
    }
    // Create combinations of sections and courses
    private static List<String[]> generateCombinations(List<String> sections, List<String> courses) {
        List<String[]> combinations = new ArrayList<>();// List to store section-course pairs
        for (String section : sections) {// Loop over sections
            for (String course : courses) {// Loop over courses
                combinations.add(new String[]{section, course});  // Add section-course pair
            }
        }
        return combinations;
    }
    // Add headers (Timeslots and Rooms) to the sheet
    private static void createSheetHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);  // Create the first row
        headerRow.createCell(0).setCellValue("Timeslots");  // First column is for timeslots
        for (int i = 0; i < rooms.size(); i++) {
            headerRow.createCell(i + 1).setCellValue(rooms.get(i));  // Add room names as headers
        }
    }
    // Generate valid assignments of sections to rooms and timeslots
    private static Map<String, String> generateValidMatching(List<String[]> sectionsCourses, List<String> rooms, List<String> timeslots) {
        Map<String, String> validMatching = new HashMap<>();  // Map to store assignments
        Map<String, List<String>> sectionTimeslotAllocated = new HashMap<>();  // Track timeslots assigned to sections
        Map<String, Set<String>> courseAssigned = new HashMap<>();  // Track courses assigned to sections
        // Initialize data for tracking assignments
        for (String[] sectionCourse : sectionsCourses) {// Loop over section-course pairs
            String section = sectionCourse[0];// Get the section
            courseAssigned.put(section, new HashSet<>());  // Initialize course set for the section
            sectionTimeslotAllocated.put(section, new ArrayList<>());// Initialize timeslot list for the section
        }
        // Assign sections and courses to rooms and timeslots
        for (String room : rooms) {
            for (String timeslot : timeslots) {
                String key = room + "-" + timeslot;  // Key is room-timeslot pair
                for (String[] sectionCourse : sectionsCourses) {// Loop over section-course pairs
                    String section = sectionCourse[0];// Get the section
                    String course = sectionCourse[1];// Get the course
                    // Check if timeslot and course are not already assigned
                    if (!sectionTimeslotAllocated.get(section).contains(timeslot) && !courseAssigned.get(section).contains(course)) {// If not assigned
                        validMatching.put(key, section + " " + course);  // Assign section-course to room-timeslot
                        sectionTimeslotAllocated.get(section).add(timeslot);  // Mark timeslot as assigned
                        courseAssigned.get(section).add(course);  // Mark course as assigned
                        break;
                    }
                }
            }
        }

        return validMatching;  // Return the valid assignments
    }
    // Fill the timetable in the Excel sheet
    private static void fillTimetable(Sheet sheet, Map<String, String> validMatching) {
        int rowNum = 1;  // Start filling data from row 1
        for (String timeslot : timeslots) {
            Row row = sheet.createRow(rowNum++);  // Create a new row
            row.createCell(0).setCellValue(timeslot);  // Add timeslot to the first column
            for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
                String room = rooms.get(roomIndex);
                String key = room + "-" + timeslot;  // Room-timeslot key
                String value = validMatching.get(key);  // Get the assigned section-course
                Cell cell = row.createCell(roomIndex + 1);  // Create a cell for the room
                if (value != null) {
                    cell.setCellValue(value);  // Add the section-course to the cell
                    // Get the semester from the section (e.g., "7A" -> "7")
                    String semester = value.split(" ")[0].substring(0, 1);
                    String colorHex = semesterColors.get(semester);  // Get the color for the semester
                    if (colorHex != null) {
                        // Apply color to the cell
                        CellStyle style = sheet.getWorkbook().createCellStyle();
                        XSSFColor color = new XSSFColor(hexToRgb(colorHex));  // Convert hex to RGB
                        style.setFillForegroundColor(color);// Set the fill color
                        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);// Set the fill pattern
                        cell.setCellStyle(style);// Apply the style to the cell
                    }
                }
            }
        }
    }
    // Convert a hex color string to an RGB byte array
    private static byte[] hexToRgb(String colorHex) {
        Color color = Color.decode(colorHex);  // Convert hex to Color object
        return new byte[]{
            (byte) color.getRed(),  // Red value
            (byte) color.getGreen(),  // Green value
            (byte) color.getBlue()  // Blue value
        };
    }
}
