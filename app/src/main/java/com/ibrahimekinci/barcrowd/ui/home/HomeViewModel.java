package com.ibrahimekinci.barcrowd.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.usecase.GetHomePageVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetRecentLiveUpdatesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SyncHomeDataUseCase;

import java.util.List;

/**
 * ViewModel for the HomeFragment.
 */
public class HomeViewModel extends ViewModel {

    private final GetHomePageVenuesUseCase getHomePageVenuesUseCase;
    private final GetRecentLiveUpdatesUseCase getRecentLiveUpdatesUseCase;
    private final SyncHomeDataUseCase syncHomeDataUseCase;

    private final LiveData<List<Venue>> homePageVenues;
    private final LiveData<List<LiveUpdate>> recentLiveUpdates;
    public HomeViewModel(GetHomePageVenuesUseCase getHomePageVenuesUseCase,
                         GetRecentLiveUpdatesUseCase getRecentLiveUpdatesUseCase,
                         SyncHomeDataUseCase syncHomeDataUseCase) {
        this.getHomePageVenuesUseCase = getHomePageVenuesUseCase;
        this.getRecentLiveUpdatesUseCase = getRecentLiveUpdatesUseCase;
        this.syncHomeDataUseCase = syncHomeDataUseCase;
        this.homePageVenues = this.getHomePageVenuesUseCase.execute();
        this.recentLiveUpdates = this.getRecentLiveUpdatesUseCase.execute();
        refreshData();
    }

    public void refreshData() {
        syncHomeDataUseCase.execute();
    }

    public LiveData<List<Venue>> getHomePageVenues() {
        return homePageVenues;
    }

    public LiveData<List<LiveUpdate>> getRecentLiveUpdates() {
        return recentLiveUpdates;
    }
}