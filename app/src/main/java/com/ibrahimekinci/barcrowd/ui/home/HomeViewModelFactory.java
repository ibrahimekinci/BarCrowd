package com.ibrahimekinci.barcrowd.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.domain.usecase.GetHomePageVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetRecentLiveUpdatesUseCase;

/**
 * Factory for creating HomeViewModel instances with required dependencies.
 */
public class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final GetHomePageVenuesUseCase getHomePageVenuesUseCase;
    private final GetRecentLiveUpdatesUseCase getRecentLiveUpdatesUseCase;

    public HomeViewModelFactory(GetHomePageVenuesUseCase getHomePageVenuesUseCase, GetRecentLiveUpdatesUseCase getRecentLiveUpdatesUseCase) {
        this.getHomePageVenuesUseCase = getHomePageVenuesUseCase;
        this.getRecentLiveUpdatesUseCase = getRecentLiveUpdatesUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(getHomePageVenuesUseCase, getRecentLiveUpdatesUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}