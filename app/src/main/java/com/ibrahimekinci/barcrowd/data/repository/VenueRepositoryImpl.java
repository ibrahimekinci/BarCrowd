package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.VenueDao;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.data.mapper.VenueMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.model.VenueFilterOptions;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class VenueRepositoryImpl implements VenueRepository {

    private final VenueDao venueDao;
    private final FirestoreWrapper firestore;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public VenueRepositoryImpl(AppDatabase db, FirestoreWrapper firestore) {
        this.venueDao = db.venueDao();
        this.firestore = firestore;
    }

    @Override
    public LiveData<List<Venue>> getAllVenues() {
        return Transformations.map(venueDao.getAllVenues(), entities ->
                entities.stream().map(VenueMapper::toModel).collect(Collectors.toList()));
    }

    @Override
    public LiveData<List<Venue>> getHomePageVenues() {
        return Transformations.map(venueDao.getHomePageVenues(), entities ->
                entities.stream().map(VenueMapper::toModel).collect(Collectors.toList()));
    }

    @Override
    public Venue getVenueById(String venueId) {
        VenueEntity entity = venueDao.getVenueById(venueId);
        return VenueMapper.toModel(entity);
    }

    @Override
    public void syncVenues() {
        firestore.getDb().collection("Venues").get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots != null) {
                        List<Venue> venues = snapshots.toObjects(Venue.class);
                        executor.execute(() -> {
                            List<VenueEntity> entities = new ArrayList<>();
                            for (Venue v : venues) entities.add(VenueMapper.toEntity(v));
                            venueDao.insertVenues(entities);
                        });
                    }
                })
                .addOnFailureListener(e -> AppLogger.e("Sync venues failed", e));
    }

    @Override
    public LiveData<List<Venue>> searchVenues(VenueFilterOptions filters) {
        MutableLiveData<List<Venue>> results = new MutableLiveData<>();

        // 1. Fetch ALL venues from Firestore (Since we need 'Contains' logic)
        // Note: For production with thousands of venues, use Algolia.
        // For this project, fetching all (small dataset) and filtering client-side is acceptable.
        Query query = firestore.getDb().collection("Venues");

        query.get().addOnSuccessListener(snapshots -> {
            List<Venue> filteredList = new ArrayList<>();
            if (snapshots != null) {
                List<Venue> rawList = snapshots.toObjects(Venue.class);

                for (Venue v : rawList) {
                    boolean matches = true;

                    // 2. Filter by Name (Case-insensitive Contains)
                    if (filters.getVenueNameQuery() != null && !filters.getVenueNameQuery().isEmpty()) {
                        String queryText = filters.getVenueNameQuery().toLowerCase();
                        String venueName = v.getName().toLowerCase();

                        // "LIKE %name%" logic
                        if (!venueName.contains(queryText)) {
                            matches = false;
                        }
                    }

                    // 3. Other Filters
                    if (matches && !filters.getVenueType().equals("Any") &&
                            (v.getType() == null || !v.getType().equalsIgnoreCase(filters.getVenueType()))) {
                        matches = false;
                    }

                    if (matches && !filters.getLastLiveUpdateCrowdLevel().equals("Any") &&
                            (v.getLastLiveUpdateCrowdLevel() == null || !v.getLastLiveUpdateCrowdLevel().equalsIgnoreCase(filters.getLastLiveUpdateCrowdLevel()))) {
                        matches = false;
                    }

                    if (matches && !filters.getLastLiveUpdateWaitTime().equals("Any") &&
                            (v.getLastLiveUpdateWaitTime() == null || !v.getLastLiveUpdateWaitTime().equalsIgnoreCase(filters.getLastLiveUpdateWaitTime()))) {
                        matches = false;
                    }

                    if (matches && !filters.getLastLiveUpdateAgeRange().equals("Any") &&
                            (v.getLastLiveUpdateAgeRange() == null || !v.getLastLiveUpdateAgeRange().equalsIgnoreCase(filters.getLastLiveUpdateAgeRange()))) {
                        matches = false;
                    }

                    if (matches) filteredList.add(v);
                }
            }
            results.setValue(filteredList);
        }).addOnFailureListener(e -> {
            AppLogger.e("Search failed", e);
            results.setValue(new ArrayList<>());
        });

        return results;
    }

    @Override
    public void updateVenueStats(String venueId, String crowd, String wait, String age) {
        if (venueId == null) return;

        Timestamp now = Timestamp.now();

        Map<String, Object> updates = new HashMap<>();
        updates.put("lastLiveUpdateCrowdLevel", crowd);
        updates.put("lastLiveUpdateWaitTime", wait);
        updates.put("lastLiveUpdateAgeRange", age);
        updates.put("lastLiveUpdateCreatedAt", now);

        // Update Firestore
        firestore.getDb().collection("Venues").document(venueId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    AppLogger.d("Venue stats updated in Firestore for " + venueId);
                    // Update Local DB
                    executor.execute(() -> {
                        venueDao.updateVenueStats(venueId, crowd, wait, age, now.toDate());
                    });
                })
                .addOnFailureListener(e -> AppLogger.e("Failed to update venue stats", e));
    }
}