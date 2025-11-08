package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.VenueDao;
import com.ibrahimekinci.barcrowd.data.local.VenueEntity;
import com.ibrahimekinci.barcrowd.data.mapper.VenueMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.model.VenueFilterOptions;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.util.ArrayList;
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
        new Thread(() -> {
            dao.insertAll(entities);
            AppLogger.d("Inserted " + venues.size() + " venues locally");
        }).start();
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
    public LiveData<List<Venue>> searchVenues(VenueFilterOptions filters) {
        SimpleSQLiteQuery query = buildFilterQuery(filters);
        return Transformations.map(dao.searchVenuesWithFilters(query), entities ->
                entities.stream()
                        .map(VenueMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    private SimpleSQLiteQuery buildFilterQuery(VenueFilterOptions filters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM venues WHERE 1=1");
        List<Object> args = new ArrayList<>();

        if (filters.getNameQuery() != null && !filters.getNameQuery().isEmpty()) {
            sql.append(" AND name LIKE ?");
            args.add("%" + filters.getNameQuery() + "%");
        }
        if (filters.getType() != null && !filters.getType().isEmpty()) {
            sql.append(" AND type = ?");
            args.add(filters.getType());
        }
        // IMPORTANT: PDF uses 'last' values, so query 'lastCrowdLevel', not 'average'
        if (filters.getCrowdLevel() != null && !filters.getCrowdLevel().isEmpty()) {
            sql.append(" AND lastCrowdLevel = ?");
            args.add(filters.getCrowdLevel());
        }
        if (filters.getWaitTime() != null && !filters.getWaitTime().isEmpty()) {
            sql.append(" AND lastWaitTime = ?");
            args.add(filters.getWaitTime());
        }
        // IMPORTANT: PDF uses 'last' age range, so query 'mostPopulousAge'
        if (filters.getAgeRange() != null && !filters.getAgeRange().isEmpty()) {
            sql.append(" AND mostPopulousAge = ?");
            args.add(filters.getAgeRange());
        }

        sql.append(" ORDER BY name ASC");
        return new SimpleSQLiteQuery(sql.toString(), args.toArray());
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