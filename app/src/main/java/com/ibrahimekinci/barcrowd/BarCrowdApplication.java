package com.ibrahimekinci.barcrowd;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.util.ConnectivityReceiver;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Application class to initialize dependencies and handle global state.
 */
public class BarCrowdApplication extends Application {
    private DependencyInjector injector;

    @Override
    public void onCreate() {
        super.onCreate();
        injector = new DependencyInjector();
        DependencyInjector.init(this);

        // Register connectivity receiver for auto-sync
        registerReceiver(new ConnectivityReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Seed sample data (run only in debug mode)
        // seedSampleData();

    }

    public DependencyInjector getDependencyInjector() {
        return injector;
    }

    private void seedSampleData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Seed users
        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", "user1");
        user1.put("email", "john.doe@example.com");
        user1.put("createdAt", 1730026983000L); // Approx Oct 27, 2025
        db.collection("users").document("user1").set(user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", "user2");
        user2.put("email", "jane.smith@example.com");
        user2.put("createdAt", 1730026984000L);
        db.collection("users").document("user2").set(user2);

        // Seed venues (using vanue01.jpg and vanue02.jpg)
        Map<String, Object> venue1 = new HashMap<>();
        venue1.put("id", "venue1");
        venue1.put("name", "The Rusty Anchor");
        venue1.put("address", "123 Flinders St, Melbourne VIC 3000");
        venue1.put("latitude", -37.8173);
        venue1.put("longitude", 144.9552);
        venue1.put("description", "A cozy pub with live music on weekends.");
        venue1.put("photoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/vanue01.jpg?alt=media&token=e68f5137-a348-42cd-b5d5-37601be76780");
        db.collection("venues").document("venue1").set(venue1);

        Map<String, Object> venue2 = new HashMap<>();
        venue2.put("id", "venue2");
        venue2.put("name", "Neon Nights Club");
        venue2.put("address", "45 Collins St, Melbourne VIC 3000");
        venue2.put("latitude", -37.8150);
        venue2.put("longitude", 144.9661);
        venue2.put("description", "Vibrant nightclub with top DJs.");
        venue2.put("photoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/vanue02.jpg?alt=media&token=7dc1261c-436d-4454-bade-3d21232effdf");
        db.collection("venues").document("venue2").set(venue2);

        // Seed live updates (using liveupdate01.mp4, liveupdate02.mp4, liveupdate03.mp4)
        Map<String, Object> update1 = new HashMap<>();
        update1.put("id", "update1");
        update1.put("venueId", "venue1");
        update1.put("userId", "user1");
        update1.put("timestamp", 1730026985000L);
        update1.put("crowdLevel", "medium");
        update1.put("waitTime", 15);
        update1.put("ageRange", "20-30");
        update1.put("photoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/live_update_photo01.jpg?alt=media&token=e68f5137-a348-42cd-b5d5-37601be76780"); // Placeholder, replace with actual photo
        update1.put("videoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/liveupdate01.mp4?alt=media&token=f2a3ea6f-157a-4a31-a9bf-a52c69cb46d0");
        update1.put("latitude", -37.8173);
        update1.put("longitude", 144.9552);
        db.collection("live_updates").document("update1").set(update1);

        Map<String, Object> update2 = new HashMap<>();
        update2.put("id", "update2");
        update2.put("venueId", "venue2");
        update2.put("userId", "user2");
        update2.put("timestamp", 1730026986000L);
        update2.put("crowdLevel", "high");
        update2.put("waitTime", 30);
        update2.put("ageRange", "25-35");
        update2.put("photoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/live_update_photo02.jpg?alt=media&token=7dc1261c-436d-4454-bade-3d21232effdf"); // Placeholder, replace with actual photo
        update2.put("videoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/liveupdate02.mp4?alt=media&token=2e7a8fa0-930b-4aeb-97a6-ff712d5898b6");
        update2.put("latitude", -37.8150);
        update2.put("longitude", 144.9661);
        db.collection("live_updates").document("update2").set(update2);

        Map<String, Object> update3 = new HashMap<>();
        update3.put("id", "update3");
        update3.put("venueId", "venue1");
        update3.put("userId", "user2");
        update3.put("timestamp", 1730026987000L);
        update3.put("crowdLevel", "low");
        update3.put("waitTime", 5);
        update3.put("ageRange", "20-30");
        update3.put("photoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/live_update_photo03.jpg?alt=media&token=c6dc4ddf-14b7-4993-97d0-e997106a7ecc"); // Placeholder, replace with actual photo
        update3.put("videoUrl", "https://firebasestorage.googleapis.com/v0/b/barcrowd-storage-2025.appspot.com/o/liveupdate03.mp4?alt=media&token=6c373ae3-8a24-46c6-a7c3-5ec9658e2782");
        update3.put("latitude", -37.8173);
        update3.put("longitude", 144.9552);
        db.collection("live_updates").document("update3").set(update3);
    }
}