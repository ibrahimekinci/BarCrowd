package com.ibrahimekinci.barcrowd.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.usecase.GetHomePageVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetRecentLiveUpdatesUseCase;

import java.util.List;

/**
 * ViewModel for the HomeFragment.
 * Manages the data state for the home screen, providing lists of
 * featured venues and recent live updates.
 */
public class HomeViewModel extends ViewModel {

    private final GetHomePageVenuesUseCase getHomePageVenuesUseCase;
    private final GetRecentLiveUpdatesUseCase getRecentLiveUpdatesUseCase;

    // LiveData streams exposed to the Fragment
    private final LiveData<List<Venue>> homePageVenues;
    private final LiveData<List<LiveUpdate>> recentLiveUpdates;

    public HomeViewModel(GetHomePageVenuesUseCase getHomePageVenuesUseCase, GetRecentLiveUpdatesUseCase getRecentLiveUpdatesUseCase) {
        this.getHomePageVenuesUseCase = getHomePageVenuesUseCase;
        this.getRecentLiveUpdatesUseCase = getRecentLiveUpdatesUseCase;

        // Initialize the data streams when the ViewModel is created
        this.homePageVenues = this.getHomePageVenuesUseCase.execute();
        this.recentLiveUpdates = this.getRecentLiveUpdatesUseCase.execute();
    }

    // Getters for the Fragment to observe
    public LiveData<List<Venue>> getHomePageVenues() {
        return homePageVenues;
    }

    public LiveData<List<LiveUpdate>> getRecentLiveUpdates() {
        return recentLiveUpdates;
    }
}