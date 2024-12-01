package com.example;

public class App {// App class to run the timetable generator
    public static void main(String[] args) {
        try {
            TimetableGenerator generator = new TimetableGenerator();// Create a new timetable generator
            generator.generateTimetable();// Generate the timetable
        } catch (Exception e) {// Catch any exceptions
            e.printStackTrace();// Print the exception
        }
    }
}
