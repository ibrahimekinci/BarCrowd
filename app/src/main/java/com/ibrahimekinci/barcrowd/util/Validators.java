package com.ibrahimekinci.barcrowd.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Validators {

    // --- Allowed Value Sets (from Schema) ---
    // Note: These are case-sensitive
    private static final Set<String> CROWD_LEVELS = new HashSet<>(
            Arrays.asList("Low", "Medium", "High")
    );

    private static final Set<String> WAIT_TIMES = new HashSet<>(
            Arrays.asList("0–5", "5–15", "15–30", "30–45", "45+")
    );

    private static final Set<String> AGE_RANGES = new HashSet<>(
            Arrays.asList("18–21", "21–24", "25–30", "30–35", "35+")
    );
    // --- End Allowed Values ---

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE
    );
    private static final int NAME_MAX_LENGTH = 50;

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks for a valid name.
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= NAME_MAX_LENGTH;
    }

    public static boolean isValidPassword(String password) {
        // At least 8 chars, one uppercase, one number
        return password != null && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*");
    }

    /**
     * Validates against the exact allowed strings from the schema.
     */
    public static boolean isValidAgeRange(String ageRange) {
        return ageRange != null && AGE_RANGES.contains(ageRange);
    }

    /**
     * Validates against the exact allowed strings from the schema.
     */
    public static boolean isValidCrowdLevel(String crowdLevel) {
        return crowdLevel != null && CROWD_LEVELS.contains(crowdLevel);
    }

    /**
     * Validates against the exact allowed strings from the schema.
     */
    public static boolean isValidWaitTime(String waitTime) {
        return waitTime != null && WAIT_TIMES.contains(waitTime);
    }

    public static boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }
}