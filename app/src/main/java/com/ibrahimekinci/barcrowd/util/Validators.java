package com.ibrahimekinci.barcrowd.util;

import java.util.regex.Pattern;

public class Validators {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final int NAME_MAX_LENGTH = 50;
    private static final int AGE_RANGE_MAX_LENGTH = 10; // e.g., "20-30"
    private static final int CROWD_LEVEL_OPTIONS = 3; // low, medium, high

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= NAME_MAX_LENGTH && name.matches("^[a-zA-Z\\s]+$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*");
    }

    public static boolean isValidAgeRange(String ageRange) {
        return ageRange != null && !ageRange.trim().isEmpty() && ageRange.length() <= AGE_RANGE_MAX_LENGTH && ageRange.matches("^\\d{2}-\\d{2}$");
    }

    public static boolean isValidCrowdLevel(String crowdLevel) {
        return crowdLevel != null && (crowdLevel.equals("low") || crowdLevel.equals("medium") || crowdLevel.equals("high"));
    }

    public static boolean isValidWaitTime(int waitTime) {
        return waitTime >= 0 && waitTime <= 120; // Reasonable limit in minutes
    }

    public static boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }
}