package com.ibrahimekinci.barcrowd.debug;

import com.google.firebase.Timestamp;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Debug utility to populate Firestore with sample data.
 * Seeds: 5 Venues, 2 Users, and 7 Live Updates.
 */
public class SampleDataSeeder {

    private final FirestoreWrapper firestore;
    private final String TOKEN = "b1624fec-aa32-4eab-b641-834cae1d7bda";

    public SampleDataSeeder(FirestoreWrapper firestore) {
        this.firestore = firestore;
    }

    public void seedData() {
        // Check if Venues collection is empty to avoid duplicates
        firestore.getDb().collection("Venues").limit(1).get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        AppLogger.i("No data found. Seeding sample data...");
                        createData();
                    } else {
                        AppLogger.d("Sample data already exists. Skipping seeding.");
                    }
                })
                .addOnFailureListener(e -> AppLogger.e("Failed to check for existing data", e));
    }

    private void createData() {
        List<Venue> venues = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<LiveUpdate> updates = new ArrayList<>();

        // --- 1. Create 2 Sample Users ---
        // Using thumbnail images as profile photos for testing validity
        User user1 = createUser("user_alice", "alicew", "Alice Walker", "alice@test.com", "thumbnail1.jpg");
        User user2 = createUser("user_bob", "bobby_j", "Bob Jones", "bob@test.com", "thumbnail2.jpg");

        users.add(user1);
        users.add(user2);

        // --- 2. Create 5 Venues ---

        // Venue 1: Revolver Upstairs (Club)
        Venue v1 = createVenue("venue_revolver", "Revolver Upstairs", "Club",
                "229 Chapel St, Prahran VIC 3181", -37.8482, 144.9936, "venue1.jpg");
        setVenueStats(v1, "High", "30–45", "21–24");
        venues.add(v1);

        // Venue 2: Hotel Esplanade (Pub)
        Venue v2 = createVenue("venue_espy", "Hotel Esplanade", "Pub",
                "11 The Esplanade, St Kilda VIC 3182", -37.8679, 144.9741, "venue2.jpg");
        setVenueStats(v2, "Medium", "15–30", "25–30");
        venues.add(v2);

        // Venue 3: Arbory Afloat (Bar)
        Venue v3 = createVenue("venue_arbory", "Arbory Afloat", "Bar",
                "2 Flinders Walk, Melbourne VIC 3000", -37.8201, 144.9676, "venue3.jpg");
        setVenueStats(v3, "Low", "0–5", "30–35");
        venues.add(v3);

        // Venue 4: Garden State Hotel (Pub)
        Venue v4 = createVenue("venue_garden", "Garden State Hotel", "Pub",
                "101 Flinders Ln, Melbourne VIC 3000", -37.8163, 144.9715, "venue4.jpg");
        setVenueStats(v4, "Medium", "5–15", "21–24");
        venues.add(v4);

        // Venue 5: Storyville (Club)
        Venue v5 = createVenue("venue_storyville", "Storyville", "Club",
                "185 Lonsdale St, Melbourne VIC 3000", -37.8112, 144.9668, "venue5.jpg");
        setVenueStats(v5, "High", "45+", "18–21");
        venues.add(v5);

        // --- 3. Create 7 Live Updates (Distributed between Users) ---

        Timestamp t1 = getTimestampMinutesAgo(120);
        Timestamp t2 = getTimestampMinutesAgo(10);
        Timestamp t3 = getTimestampMinutesAgo(60);
        Timestamp t4 = getTimestampMinutesAgo(5);
        Timestamp t5 = getTimestampMinutesAgo(15);
        Timestamp t6 = getTimestampMinutesAgo(30);
        Timestamp t7 = getTimestampMinutesAgo(2);

        // Updates for Venue 1 (Revolver)
        updates.add(createUpdate(v1, user1, "1", "Low", "0–5", "21–24", "Starting to fill up.", t1));
        updates.add(createUpdate(v1, user2, "2", "High", "30–45", "21–24", "Packed now! Great music.", t2));

        // Updates for Venue 2 (Espy)
        updates.add(createUpdate(v2, user1, "3", "Low", "5–15", "25–30", "Chill vibes by the window.", t3));
        updates.add(createUpdate(v2, user2, "4", "Medium", "15–30", "25–30", "Getting busier, line forming.", t4));

        // Update for Venue 3 (Arbory)
        updates.add(createUpdate(v3, user1, "5", "Low", "0–5", "30–35", "Perfect sunny spot, plenty of seats.", t5));

        // Update for Venue 4 (Garden State)
        updates.add(createUpdate(v4, user2, "6", "Medium", "5–15", "21–24", "Good crowd, fast service.", t6));

        // Update for Venue 5 (Storyville)
        updates.add(createUpdate(v5, user1, "7", "High", "45+", "18–21", "Crazy busy inside!", t7));

        // --- 4. Upload to Firestore ---

        // Upload Users
        for (User u : users) {
            firestore.setDocument("Users", u.getUserId(), u, new FirestoreWrapper.Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    AppLogger.d("Seeded User: " + u.getUsername());
                }
                @Override
                public void onFailure(Exception e) {
                    AppLogger.e("Failed to seed user", e);
                }
            });
        }

        // Upload Venues
        for (Venue v : venues) {
            firestore.setDocument("Venues", v.getVenueId(), v, new FirestoreWrapper.Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    AppLogger.d("Seeded Venue: " + v.getName());
                }
                @Override
                public void onFailure(Exception e) {
                    AppLogger.e("Failed to seed venue", e);
                }
            });
        }

        // Upload Updates
        for (LiveUpdate u : updates) {
            firestore.setDocument("LiveUpdates", u.getUpdateId(), u, new FirestoreWrapper.Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    AppLogger.d("Seeded Update: " + u.getUpdateId());
                }
                @Override
                public void onFailure(Exception e) {
                    AppLogger.e("Failed to seed update", e);
                }
            });
        }
    }

    // --- Helper Methods ---

    private User createUser(String id, String username, String fullName, String email, String photoFile) {
        User u = new User();
        u.setUserId(id);
        u.setUsername(username);
        u.setFullName(fullName);
        u.setEmail(email);
        // Reusing thumbnail images for profile photos for convenience
        u.setProfilePhotoUrl(generateUrl("test%2Fthumbnails", photoFile));
        u.setTrusted(true);
        u.setCreatedAt(Timestamp.now());
        return u;
    }

    private Venue createVenue(String id, String name, String type, String address, double lat, double lng, String logoFile) {
        Venue v = new Venue();
        v.setVenueId(id);
        v.setName(name);
        v.setType(type);
        v.setAddress(address);
        v.setLatitude(lat);
        v.setLongitude(lng);
        v.setLogoUrl(generateUrl("venues%2Flogos", logoFile));
        v.setOpeningHours(getStandardHours());
        v.setShowOnHomePage(true);
        v.setCreatedAt(Timestamp.now());
        v.setUpdatedAt(Timestamp.now());
        return v;
    }

    private void setVenueStats(Venue v, String crowd, String wait, String age) {
        v.setLastLiveUpdateCrowdLevel(crowd);
        v.setLastLiveUpdateWaitTime(wait);
        v.setLastLiveUpdateAgeRange(age);
        v.setLastLiveUpdateCreatedAt(Timestamp.now());
    }

    private LiveUpdate createUpdate(Venue v, User author, String index, String crowd, String wait, String age, String desc, Timestamp time) {
        LiveUpdate u = new LiveUpdate();
        u.setUpdateId(UUID.randomUUID().toString());
        u.setVenueId(v.getVenueId());

        // User Info
        u.setUserId(author.getUserId());
        u.setUserName(author.getUsername());
        u.setUserPhotoUrl(author.getProfilePhotoUrl());

        // Denormalized Venue Info
        u.setVenueName(v.getName());
        u.setVenueType(v.getType());
        u.setVenueLogoUrl(v.getLogoUrl());

        // Stats
        u.setCrowdLevel(crowd);
        u.setWaitTime(wait);
        u.setAgeRange(age);
        u.setDescription(desc);

        // Media Assets based on index (1 to 7)
        u.setThumbnailUrl(generateUrl("test%2Fthumbnails", "thumbnail" + index + ".jpg"));
        u.setMediaUrl(generateUrl("test%2Fvideos", "video" + index + ".mp4"));

        u.setCreatedAt(time);
        u.setDeleted(false);
        return u;
    }

    /**
     * Generates the full Firebase Storage URL based on the provided pattern and token.
     */
    private String generateUrl(String folderEncoded, String filename) {
        return "https://firebasestorage.googleapis.com/v0/b/barcrowd-5a1a9.firebasestorage.app/o/" +
                folderEncoded + "%2F" + filename + "?alt=media&token=" + TOKEN;
    }

    private Timestamp getTimestampMinutesAgo(int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -minutes);
        return new Timestamp(cal.getTime());
    }

    private Map<String, String> getStandardHours() {
        Map<String, String> hours = new HashMap<>();
        hours.put("Monday", "17:00 - 01:00");
        hours.put("Tuesday", "17:00 - 01:00");
        hours.put("Wednesday", "17:00 - 01:00");
        hours.put("Thursday", "17:00 - 03:00");
        hours.put("Friday", "16:00 - 05:00");
        hours.put("Saturday", "16:00 - 05:00");
        hours.put("Sunday", "14:00 - 01:00");
        return hours;
    }
}