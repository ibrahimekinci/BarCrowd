package com.ibrahimekinci.barcrowd.ui.venues;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.usecase.GetAllVenuesUseCase;

import java.util.List;

/**
 * ViewModel for the AllVenuesFragment.
 * Manages the data state for displaying a list of all venues.
 */
public class AllVenuesViewModel extends ViewModel {

    private final GetAllVenuesUseCase getAllVenuesUseCase;
    private final LiveData<List<Venue>> allVenues;

    public AllVenuesViewModel(GetAllVenuesUseCase getAllVenuesUseCase) {
        this.getAllVenuesUseCase = getAllVenuesUseCase;
        // Fetch all venues, sorted by name, from the use case
        this.allVenues = this.getAllVenuesUseCase.execute();
    }

    /**
     * Exposes the LiveData stream of all venues for the Fragment to observe.
     * @return LiveData<List<Venue>>
     */
    public LiveData<List<Venue>> getAllVenues() {
        return allVenues;
    }
}