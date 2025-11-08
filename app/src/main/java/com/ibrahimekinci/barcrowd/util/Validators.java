package com.ibrahimekinci.barcrowd.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Validators {

    // --- Allowed Value Sets (from Schema) ---
    private static final Set<String> CROWD_LEVELS = new HashSet<>(
            Arrays.asList("Low", "Medium", "High")
    );

    private static final Set<String> WAIT_TIMES = new HashSet<>(
            Arrays.asList("0–5", "5–15", "15–30", "30–45", "45+")
    );

    private static final Set<String> AGE_RANGES = new HashSet<>(
            Arrays.asList("18–21", "21–24", "25–30", "30–35", "35+")
    );

    // --- Patterns ---
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE
    );

    // UPDATED: Pattern to only allow letters and spaces for full name
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");

    private static final int NAME_MAX_LENGTH = 50; // As specified in original file
    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30; // From your firestore.rules

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks for a valid name. (Letters and spaces only)
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty()
                && name.length() <= NAME_MAX_LENGTH
                && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Checks for a valid username. (Length 3-30)
     */
    public static boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty()
                && username.length() >= USERNAME_MIN_LENGTH
                && username.length() <= USERNAME_MAX_LENGTH;
    }

    public static boolean isValidPassword(String password) {
        // At least 8 chars, one uppercase, one number
        return password != null && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*");
    }

    public static boolean isValidAgeRange(String ageRange) {
        return ageRange != null && AGE_RANGES.contains(ageRange);
    }

    public static boolean isValidCrowdLevel(String crowdLevel) {
        return crowdLevel != null && CROWD_LEVELS.contains(crowdLevel);
    }

    public static boolean isValidWaitTime(String waitTime) {
        return waitTime != null && WAIT_TIMES.contains(waitTime);
    }

    public static boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }
}