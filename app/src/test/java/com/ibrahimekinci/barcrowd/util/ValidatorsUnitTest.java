package com.ibrahimekinci.barcrowd.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidatorsUnitTest {
    @Test
    public void isValidEmailTrue() {
        assertTrue(Validators.isValidEmail("test@ex.com"));
    }

    @Test
    public void isValidEmailFalse() {
        assertFalse(Validators.isValidEmail("invalid@"));
        assertFalse(Validators.isValidEmail(null));
    }

    @Test
    public void isValidNameTrue() {
        assertTrue(Validators.isValidName("Bar Name"));
    }

    @Test
    public void isValidNameFalse() {
        assertFalse(Validators.isValidName("Special@Name"));
        assertFalse(Validators.isValidName(""));
        assertFalse(Validators.isValidName(new String(new char[51]).replace('\0', 'a')));
    }

    @Test
    public void isValidPasswordTrue() {
        assertTrue(Validators.isValidPassword("Pass123A"));
    }

    @Test
    public void isValidPasswordFalse() {
        assertFalse(Validators.isValidPassword("short"));
        assertFalse(Validators.isValidPassword("pass123a"));
        assertFalse(Validators.isValidPassword("PassA"));
    }

    @Test
    public void isValidAgeRangeTrue() {
        assertTrue(Validators.isValidAgeRange("20-30"));
    }

    @Test
    public void isValidAgeRangeFalse() {
        assertFalse(Validators.isValidAgeRange("invalid"));
        assertFalse(Validators.isValidAgeRange(""));
        assertFalse(Validators.isValidAgeRange(new String(new char[11]).replace('\0', '1')));
    }

    @Test
    public void isValidCrowdLevelTrue() {
        assertTrue(Validators.isValidCrowdLevel("low"));
        assertTrue(Validators.isValidCrowdLevel("medium"));
        assertTrue(Validators.isValidCrowdLevel("high"));
    }

    @Test
    public void isValidCrowdLevelFalse() {
        assertFalse(Validators.isValidCrowdLevel("invalid"));
        assertFalse(Validators.isValidCrowdLevel(null));
    }

    @Test
    public void isValidWaitTimeTrue() {
        assertTrue(Validators.isValidWaitTime(0));
        assertTrue(Validators.isValidWaitTime(120));
    }

    @Test
    public void isValidWaitTimeFalse() {
        assertFalse(Validators.isValidWaitTime(-1));
        assertFalse(Validators.isValidWaitTime(121));
    }

    @Test
    public void isNotEmptyTrue() {
        assertTrue(Validators.isNotEmpty("text"));
    }

    @Test
    public void isNotEmptyFalse() {
        assertFalse(Validators.isNotEmpty(null));
        assertFalse(Validators.isNotEmpty(""));
        assertFalse(Validators.isNotEmpty(" "));
    }
}