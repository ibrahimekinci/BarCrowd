package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.VenueDao;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity; // Import new entity
import com.ibrahimekinci.barcrowd.data.mapper.VenueMapper; // Import new mapper
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import java.util.List;
import java.util.stream.Collectors;

public class VenueRepositoryImpl implements VenueRepository {
    private final VenueDao dao;
    private final FirestoreWrapper firestore;

    public VenueRepositoryImpl(AppDatabase db, FirestoreWrapper firestore) {
        this.dao = db.venueDao();
        this.firestore = firestore;
    }

    /**
     * Inserts a list of domain models by mapping them to entities.
     * This is now private, only used by syncVenues.
     */
    private void insertVenues(List<Venue> venues) {
        // Use the new VenueMapper to convert Venue to VenueEntity
        List<VenueEntity> entities = venues.stream()
                .map(VenueMapper::toEntity)
                .collect(Collectors.toList());
        dao.insertAll(entities);
        AppLogger.d("Inserted " + venues.size() + " venues locally");
    }

    @Override
    public LiveData<List<Venue>> getHomePageVenues() {
        syncVenues(); // Trigger background sync
        // Use the new DAO method and the new VenueMapper
        return Transformations.map(dao.getHomePageVenues(), entities ->
                entities.stream()
                        .map(VenueMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Venue getVenueById(String venueId) {
        // Use the new VenueMapper
        return VenueMapper.toModel(dao.getVenueById(venueId));
    }

    @Override
    public LiveData<List<Venue>> searchVenues(String query) {
        // Use the new VenueMapper
        return Transformations.map(dao.searchVenues(query), entities ->
                entities.stream()
                        .map(VenueMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void syncVenues() {
        firestore.listenForChanges("Venues", null, null, snapshots -> {
            if (snapshots == null) return;

            // Map all documents from Firestore to Venue domain models
            List<Venue> venues = snapshots.getDocuments().stream()
                    .map(doc -> doc.toObject(Venue.class))
                    .collect(Collectors.toList());

            // Insert the domain models (which will be mapped to entities)
            if (!venues.isEmpty()) {
                insertVenues(venues);
                AppLogger.i("Synced " + venues.size() + " venues from Firestore");
            }
        });
    }

    @Override
    public LiveData<List<Venue>> getAllVenuesSortedByName() {
        // We still sync all venues, as this list is expected to be complete.
        syncVenues();

        return Transformations.map(dao.getAllVenuesSortedByName(), entities ->
                entities.stream()
                        .map(VenueMapper::toModel)
                        .collect(Collectors.toList())
        );
    }
}