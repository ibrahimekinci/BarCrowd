package com.ibrahimekinci.barcrowd.debug;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to seed Firestore with sample data for debugging.
 * Includes a check to prevent duplicate seeding.
 */
public class SampleDataSeeder {

    private static final String TAG = "SampleDataSeeder";

    // --- PASTE YOUR 10 NEW LINKS HERE ---
    // Make sure to match the file names from your screenshot

    // Using the new project links: barcrowd-5a1a9
    private static final String VENUE_1_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/venue1.jpg?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String VENUE_2_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/venue2.jpg?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String VENUE_3_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/venue3.jpg?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String VENUE_4_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/venue4.jpg?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String VENUE_5_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/venue5.jpg?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";

    private static final String UPDATE_1_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/liveupdate1.mp4?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String UPDATE_2_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/liveupdate2.mp4?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String UPDATE_3_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/liveupdate3.mp4?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String UPDATE_4_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/liveupdate4.mp4?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";
    private static final String UPDATE_5_URL = "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/liveupdate5.mp4?alt=media&token=82101155-79af-4c5f-a7aa-465d652e2791";

    // --- END OF LINKS ---

    /**
     * Checks if data already exists, then executes the seeding process.
     */
    public static void seedDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document("user1-ibrahim").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && task.getResult().exists()) {
                    Log.d(TAG, "Sample data already exists. Skipping seeding.");
                } else {
                    Log.d(TAG, "No sample data found. Seeding database...");
                    performSeed(db);
                }
            } else {
                Log.e(TAG, "Failed to check for existing data.", task.getException());
            }
        });
    }

    /**
     * Performs the actual data seeding.
     */
    private static void performSeed(FirebaseFirestore db) {
        long baseTimeMillis = System.currentTimeMillis();
        seedUsers(db, baseTimeMillis);
        seedVenues(db, baseTimeMillis + 5000);
        seedLiveUpdates(db, baseTimeMillis + 10000);
        Log.d(TAG, "Sample data seeding complete.");
    }

    /**
     * Seeds 5 sample users into the /Users collection.
     */
    private static void seedUsers(FirebaseFirestore db, long startTimeMillis) {
        try {
            Map<String, Object> user1 = new HashMap<>();
            user1.put("userId", "user1-ibrahim");
            user1.put("fullName", "Ibrahim Ekinci");
            user1.put("username", "ibrahim");
            user1.put("email", "ibrahim@test.com");
            user1.put("profilePhotoUrl", VENUE_1_URL);
            user1.put("isTrusted", true);
            user1.put("isDeleted", false);
            user1.put("deletedAt", null);
            user1.put("createdAt", new Timestamp(new Date(startTimeMillis)));
            db.collection("Users").document("user1-ibrahim").set(user1);

            Map<String, Object> user2 = new HashMap<>();
            user2.put("userId", "user2-jane");
            user2.put("fullName", "Jane Smith");
            user2.put("username", "jane.smith");
            user2.put("email", "jane@test.com");
            user2.put("profilePhotoUrl", null);
            user2.put("isTrusted", false);
            user2.put("isDeleted", false);
            user2.put("deletedAt", null);
            user2.put("createdAt", new Timestamp(new Date(startTimeMillis + 1000)));
            db.collection("Users").document("user2-jane").set(user2);

            Map<String, Object> user3 = new HashMap<>();
            user3.put("userId", "user3-alex");
            user3.put("fullName", "Alex Johnson");
            user3.put("username", "alexj");
            user3.put("email", "alex@test.com");
            user3.put("profilePhotoUrl", null);
            user3.put("isTrusted", false);
            user3.put("isDeleted", false);
            user3.put("deletedAt", null);
            user3.put("createdAt", new Timestamp(new Date(startTimeMillis + 2000)));
            db.collection("Users").document("user3-alex").set(user3);

            Map<String, Object> user4 = new HashMap<>();
            user4.put("userId", "user4-mike");
            user4.put("fullName", "Michael Lee");
            user4.put("username", "mikelee");
            user4.put("email", "mike@test.com");
            user4.put("profilePhotoUrl", null);
            user4.put("isTrusted", false);
            user4.put("isDeleted", false);
            user4.put("deletedAt", null);
            user4.put("createdAt", new Timestamp(new Date(startTimeMillis + 3000)));
            db.collection("Users").document("user4-mike").set(user4);

            Map<String, Object> user5 = new HashMap<>();
            user5.put("userId", "user5-sarah");
            user5.put("fullName", "Sarah Chen");
            user5.put("username", "sarahc");
            user5.put("email", "sarah@test.com");
            user5.put("profilePhotoUrl", null);
            user5.put("isTrusted", false);
            user5.put("isDeleted", false);
            user5.put("deletedAt", null);
            user5.put("createdAt", new Timestamp(new Date(startTimeMillis + 4000)));
            db.collection("Users").document("user5-sarah").set(user5);

            Log.d(TAG, "5 users seeded successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding Users", e);
        }
    }

    /**
     * Seeds 5 sample venues into the /Venues collection.
     */
    private static void seedVenues(FirebaseFirestore db, long startTimeMillis) {
        try {
            // Venue 1: Revolver Upstairs (Club)
            Map<String, Object> venue1 = new HashMap<>();
            venue1.put("venueId", "venue1-revolver");
            venue1.put("name", "Revolver Upstairs");
            venue1.put("type", "Club");
            venue1.put("logoUrl", VENUE_1_URL);
            venue1.put("address", "229 Chapel St, Prahran VIC 3181");
            venue1.put("latitude", -37.8493);
            venue1.put("longitude", 144.9939);
            venue1.put("openingHours", new HashMap<String, String>() {{
                put("Monday", "Closed");
                put("Tuesday", "17:00–03:00");
                put("Wednesday", "17:00–03:00");
                put("Thursday", "17:00–05:00");
                put("Friday", "17:00–07:00");
                put("Saturday", "12:00–07:00");
                put("Sunday", "12:00–01:00");
            }});
            venue1.put("averageCrowdLevel", "High");
            venue1.put("lastCrowdLevel", "High");
            venue1.put("averageWaitTime", "15–30");
            venue1.put("lastWaitTime", "30–45");
            venue1.put("crowdLevelUpdatedAt", new Timestamp(new Date(startTimeMillis - 3600000)));
            venue1.put("mostPopulousAge", "25–30");
            venue1.put("showOnHomePage", true);
            venue1.put("createdAt", new Timestamp(new Date(startTimeMillis)));
            db.collection("Venues").document("venue1-revolver").set(venue1);

            // Venue 2: Section 8 (Bar)
            Map<String, Object> venue2 = new HashMap<>();
            venue2.put("venueId", "venue2-section8");
            venue2.put("name", "Section 8");
            venue2.put("type", "Bar");
            venue2.put("logoUrl", VENUE_2_URL);
            venue2.put("address", "27-29 Tattersalls Ln, Melbourne VIC 3000");
            venue2.put("latitude", -37.8105);
            venue2.put("longitude", 144.9654);
            venue2.put("openingHours", new HashMap<String, String>() {{
                put("Monday", "12:00–01:00");
                put("Tuesday", "12:00–01:00");
                put("Wednesday", "12:00–01:00");
                put("Thursday", "12:00–03:00");
                put("Friday", "12:00–03:00");
                put("Saturday", "12:00–03:00");
                put("Sunday", "12:00–01:00");
            }});
            venue2.put("averageCrowdLevel", "Medium");
            venue2.put("lastCrowdLevel", "Medium");
            venue2.put("averageWaitTime", "5–15");
            venue2.put("lastWaitTime", "5–15");
            venue2.put("crowdLevelUpdatedAt", new Timestamp(new Date(startTimeMillis - 1800000)));
            venue2.put("mostPopulousAge", "21–24");
            venue2.put("showOnHomePage", true);
            venue2.put("createdAt", new Timestamp(new Date(startTimeMillis + 1000)));
            db.collection("Venues").document("venue2-section8").set(venue2);

            // Venue 3: The Imperial Hotel (Pub)
            Map<String, Object> venue3 = new HashMap<>();
            venue3.put("venueId", "venue3-imperial");
            venue3.put("name", "The Imperial Hotel");
            venue3.put("type", "Pub");
            venue3.put("logoUrl", VENUE_3_URL);
            venue3.put("address", "2-8 Bourke St, Melbourne VIC 3000");
            venue3.put("latitude", -37.8093);
            venue3.put("longitude", 144.9705);
            venue3.put("openingHours", new HashMap<String, String>() {{
                put("Monday", "11:00–23:00");
                put("Tuesday", "11:00–23:00");
                put("Wednesday", "11:00–23:00");
                put("Thursday", "11:00–00:00");
                put("Friday", "11:00–01:00");
                put("Saturday", "11:00–01:00");
                put("Sunday", "11:00–23:00");
            }});
            venue3.put("averageCrowdLevel", "Low");
            venue3.put("lastCrowdLevel", "Low");
            venue3.put("averageWaitTime", "0–5");
            venue3.put("lastWaitTime", "0–5");
            venue3.put("crowdLevelUpdatedAt", new Timestamp(new Date(startTimeMillis - 600000)));
            venue3.put("mostPopulousAge", "30–35");
            venue3.put("showOnHomePage", true);
            venue3.put("createdAt", new Timestamp(new Date(startTimeMillis + 2000)));
            db.collection("Venues").document("venue3-imperial").set(venue3);

            // Venue 4: Cherry Bar (Bar)
            Map<String, Object> venue4 = new HashMap<>();
            venue4.put("venueId", "venue4-cherry");
            venue4.put("name", "Cherry Bar");
            venue4.put("type", "Bar");
            venue4.put("logoUrl", VENUE_4_URL);
            venue4.put("address", "68 Little Collins St, Melbourne VIC 3000");
            venue4.put("latitude", -37.8120);
            venue4.put("longitude", 144.9702);
            venue4.put("openingHours", new HashMap<String, String>() {{
                put("Monday", "17:00–03:00");
                put("Tuesday", "17:00–03:00");
                put("Wednesday", "17:00–03:00");
                put("Thursday", "17:00–05:00");
                put("Friday", "17:00–05:00");
                put("Saturday", "17:00–05:00");
                put("Sunday", "17:00–03:00");
            }});
            venue4.put("averageCrowdLevel", "Medium");
            venue4.put("lastCrowdLevel", "Medium");
            venue4.put("averageWaitTime", "15–30");
            venue4.put("lastWaitTime", "15–30");
            venue4.put("crowdLevelUpdatedAt", new Timestamp(new Date(startTimeMillis - 300000)));
            venue4.put("mostPopulousAge", "25–30");
            venue4.put("showOnHomePage", true);
            venue4.put("createdAt", new Timestamp(new Date(startTimeMillis + 3000)));
            db.collection("Venues").document("venue4-cherry").set(venue4);

            // Venue 5: New Guernica (Club)
            Map<String, Object> venue5 = new HashMap<>();
            venue5.put("venueId", "venue5-guernica");
            venue5.put("name", "New Guernica");
            venue5.put("type", "Club");
            venue5.put("logoUrl", VENUE_5_URL);
            venue5.put("address", "64 Smith St, Collingwood VIC 3066");
            venue5.put("latitude", -37.8082);
            venue5.put("longitude", 144.9839);
            venue5.put("openingHours", new HashMap<String, String>() {{
                put("Monday", "Closed");
                put("Tuesday", "Closed");
                put("Wednesday", "Closed");
                put("Thursday", "21:00–03:00");
                put("Friday", "21:00–05:00");
                put("Saturday", "21:00–05:00");
                put("Sunday", "Closed");
            }});
            venue5.put("averageCrowdLevel", "High");
            venue5.put("lastCrowdLevel", "High");
            venue5.put("averageWaitTime", "15–30");
            venue5.put("lastWaitTime", "15–30");
            venue5.put("crowdLevelUpdatedAt", new Timestamp(new Date(startTimeMillis - 120000)));
            venue5.put("mostPopulousAge", "18–21");
            venue5.put("showOnHomePage", true);
            venue5.put("createdAt", new Timestamp(new Date(startTimeMillis + 4000)));
            db.collection("Venues").document("venue5-guernica").set(venue5);

            Log.d(TAG, "5 venues seeded successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding Venues", e);
        }
    }

    /**
     * Seeds 5 sample live updates into the /LiveUpdates collection.
     * This version includes denormalized data (venueName, venueType, venueLogoUrl).
     */
    private static void seedLiveUpdates(FirebaseFirestore db, long startTimeMillis) {
        try {
            // Live Update 1 (User 1 @ Venue 1)
            Map<String, Object> update1 = new HashMap<>();
            update1.put("updateId", "update1");
            update1.put("venueId", "venue1-revolver");
            update1.put("userId", "user1-ibrahim");
            update1.put("crowdLevel", "High");
            update1.put("waitTime", "30–45");
            update1.put("ageRange", "25–30");
            update1.put("description", "Line is huge, but moving. Inside is packed.");
            update1.put("mediaUrl", UPDATE_1_URL);
            update1.put("thumbnailUrl", VENUE_1_URL);
            update1.put("isDeleted", false);
            update1.put("deletedAt", null);
            update1.put("createdAt", new Timestamp(new Date(startTimeMillis)));
            // Denormalized data
            update1.put("venueName", "Revolver Upstairs");
            update1.put("venueType", "Club");
            update1.put("venueLogoUrl", VENUE_1_URL);
            db.collection("LiveUpdates").document("update1").set(update1);

            // Live Update 2 (User 2 @ Venue 2)
            Map<String, Object> update2 = new HashMap<>();
            update2.put("updateId", "update2");
            update2.put("venueId", "venue2-section8");
            update2.put("userId", "user2-jane");
            update2.put("crowdLevel", "Medium");
            update2.put("waitTime", "5–15");
            update2.put("ageRange", "21–24");
            update2.put("description", "Super chill vibes, easy to get a drink.");
            update2.put("mediaUrl", UPDATE_2_URL);
            update2.put("thumbnailUrl", VENUE_2_URL);
            update2.put("isDeleted", false);
            update2.put("deletedAt", null);
            update2.put("createdAt", new Timestamp(new Date(startTimeMillis + 1000)));
            // Denormalized data
            update2.put("venueName", "Section 8");
            update2.put("venueType", "Bar");
            update2.put("venueLogoUrl", VENUE_2_URL);
            db.collection("LiveUpdates").document("update2").set(update2);

            // Live Update 3 (User 3 @ Venue 3)
            Map<String, Object> update3 = new HashMap<>();
            update3.put("updateId", "update3");
            update3.put("venueId", "venue3-imperial");
            update3.put("userId", "user3-alex");
            update3.put("crowdLevel", "Low");
            update3.put("waitTime", "0–5");
            update3.put("ageRange", "30–35");
            update3.put("description", "Quiet night, perfect for a pub meal.");
            update3.put("mediaUrl", UPDATE_3_URL);
            update3.put("thumbnailUrl", VENUE_3_URL);
            update3.put("isDeleted", false);
            update3.put("deletedAt", null);
            update3.put("createdAt", new Timestamp(new Date(startTimeMillis + 2000)));
            // Denormalized data
            update3.put("venueName", "The Imperial Hotel");
            update3.put("venueType", "Pub");
            update3.put("venueLogoUrl", VENUE_3_URL);
            db.collection("LiveUpdates").document("update3").set(update3);

            // Live Update 4 (User 4 @ Venue 4)
            Map<String, Object> update4 = new HashMap<>();
            update4.put("updateId", "update4");
            update4.put("venueId", "venue4-cherry");
            update4.put("userId", "user4-mike");
            update4.put("crowdLevel", "Medium");
            update4.put("waitTime", "15–30");
            update4.put("ageRange", "25–30");
            update4.put("description", "Band is setting up, decent crowd.");
            update4.put("mediaUrl", UPDATE_4_URL);
            update4.put("thumbnailUrl", VENUE_4_URL);
            update4.put("isDeleted", false);
            update4.put("deletedAt", null);
            update4.put("createdAt", new Timestamp(new Date(startTimeMillis + 3000)));
            // Denormalized data
            update4.put("venueName", "Cherry Bar");
            update4.put("venueType", "Bar");
            update4.put("venueLogoUrl", VENUE_4_URL);
            db.collection("LiveUpdates").document("update4").set(update4);

            // Live Update 5 (User 5 @ Venue 5)
            Map<String, Object> update5 = new HashMap<>();
            update5.put("updateId", "update5");
            update5.put("venueId", "venue5-guernica");
            update5.put("userId", "user5-sarah");
            update5.put("crowdLevel", "High");
            update5.put("waitTime", "15–30");
            update5.put("ageRange", "18–21");
            update5.put("description", "Place is buzzing, DJ is great!");
            update5.put("mediaUrl", UPDATE_5_URL);
            update5.put("thumbnailUrl", VENUE_5_URL); // Fixed typo from 'httpsS'
            update5.put("isDeleted", false);
            update5.put("deletedAt", null);
            update5.put("createdAt", new Timestamp(new Date(startTimeMillis + 4000)));
            // Denormalized data
            update5.put("venueName", "New Guernica");
            update5.put("venueType", "Club");
            update5.put("venueLogoUrl", VENUE_5_URL);
            db.collection("LiveUpdates").document("update5").set(update5);

            Log.d(TAG, "5 live updates seeded successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding LiveUpdates", e);
        }
    }
}