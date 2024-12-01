package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    @Test
    public void testApp() {
        // Simple test to see if App runs without errors
        assertDoesNotThrow(() -> {
            App.main(new String[] {});
        });
    }
}
