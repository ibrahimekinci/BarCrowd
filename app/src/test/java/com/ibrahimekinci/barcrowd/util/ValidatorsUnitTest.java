package com.ibrahimekinci.barcrowd.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorsUnitTest {
    @Test
    public void isValidEmailTrue() {
        assertTrue(Validators.isValidEmail("test@example.com"));
    }

    @Test
    public void isValidEmailFalse() {
        assertFalse(Validators.isValidEmail("invalid@"));
        assertFalse(Validators.isValidEmail(null));
        assertFalse(Validators.isValidEmail("test.com"));
    }

    @Test
    public void isValidNameTrue() {
        assertTrue(Validators.isValidName("Revolver Upstairs"));
    }

    @Test
    public void isValidNameFalse() {
        assertFalse(Validators.isValidName("")); // Empty
        assertFalse(Validators.isValidName("   ")); // Blank
        assertFalse(Validators.isValidName(null));
        // Test max length
        assertFalse(Validators.isValidName("a".repeat(51)));
    }

    @Test
    public void isValidPasswordTrue() {
        assertTrue(Validators.isValidPassword("Pass12345"));
    }

    @Test
    public void isValidPasswordFalse() {
        assertFalse(Validators.isValidPassword("short")); // Too short
        assertFalse(Validators.isValidPassword("password123")); // No uppercase
        assertFalse(Validators.isValidPassword("PasswordABC")); // No number
        assertFalse(Validators.isValidPassword(null));
    }

    @Test
    public void isValidAgeRangeTrue() {
        assertTrue(Validators.isValidAgeRange("18–21"));
        assertTrue(Validators.isValidAgeRange("35+"));
    }

    @Test
    public void isValidAgeRangeFalse() {
        assertFalse(Validators.isValidAgeRange("18-21")); // Wrong dash
        assertFalse(Validators.isValidAgeRange("20-30")); // Not in set
        assertFalse(Validators.isValidAgeRange("low"));
        assertFalse(Validators.isValidAgeRange(null));
    }

    @Test
    public void isValidCrowdLevelTrue() {
        assertTrue(Validators.isValidCrowdLevel("Low"));
        assertTrue(Validators.isValidCrowdLevel("Medium"));
        assertTrue(Validators.isValidCrowdLevel("High"));
    }

    @Test
    public void isValidCrowdLevelFalse() {
        assertFalse(Validators.isValidCrowdLevel("low")); // Case-sensitive
        assertFalse(Validators.isValidCrowdLevel("Full"));
        assertFalse(Validators.isValidCrowdLevel(null));
    }

    @Test
    public void isValidWaitTimeTrue() {
        assertTrue(Validators.isValidWaitTime("0–5"));
        assertTrue(Validators.isValidWaitTime("45+"));
    }

    @Test
    public void isValidWaitTimeFalse() {
        assertFalse(Validators.isValidWaitTime("0-5")); // Wrong dash
        assertFalse(Validators.isValidWaitTime("10")); // Not in set
        assertFalse(Validators.isValidWaitTime(null));
    }

    @Test
    public void isNotEmptyTrue() {
        assertTrue(Validators.isNotEmpty("text"));
    }

    @Test
    public void isNotEmptyFalse() {
        assertFalse(Validators.isNotEmpty(null));
        assertFalse(Validators.isNotEmpty(""));
        assertFalse(Validators.isNotEmpty("   ")); // Whitespace only
    }
}