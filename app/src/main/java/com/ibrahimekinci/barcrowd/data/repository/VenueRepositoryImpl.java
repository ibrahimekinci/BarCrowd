package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.VenueDao;
import com.ibrahimekinci.barcrowd.data.mapper.VenueMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.google.firebase.firestore.DocumentChange;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class VenueRepositoryImpl implements VenueRepository {
    private VenueDao dao;
    private FirestoreWrapper firestore;

    public VenueRepositoryImpl(AppDatabase db, FirestoreWrapper firestore) {
        this.dao = db.venueDao();
        this.firestore = firestore;
    }

    @Override
    public void insertVenues(List<Venue> venues) {
        dao.insertAll(venues.stream().map(VenueMapper::toEntity).collect(Collectors.toList()));
        AppLogger.d("Inserted " + venues.size() + " venues locally");
    }

    @Override
    public LiveData<List<Venue>> getAllVenues() {
        syncVenues();
        return Transformations.map(dao.getAllVenues(), entities -> entities.stream().map(VenueMapper::toModel).collect(Collectors.toList()));
    }

    @Override
    public Venue getVenueById(String venueId) {
        return VenueMapper.toModel(dao.getVenueById(venueId));
    }

    @Override
    public LiveData<List<Venue>> searchVenues(String search) {
        return Transformations.map(dao.searchVenues(search), entities -> entities.stream().map(VenueMapper::toModel).collect(Collectors.toList()));
    }

    @Override
    public void syncVenues() {
        firestore.listenForChanges("venues", null, null, snapshots -> {  // Updated to use wrapper with null field
            List<Venue> venues = snapshots.getDocuments().stream()
                    .map(doc -> doc.toObject(Venue.class))
                    .collect(Collectors.toList());
            insertVenues(venues);
            AppLogger.i("Synced " + venues.size() + " venues from Firestore");
        });
    }
}