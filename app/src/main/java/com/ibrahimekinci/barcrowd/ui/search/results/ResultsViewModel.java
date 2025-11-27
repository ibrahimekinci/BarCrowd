package com.ibrahimekinci.barcrowd.ui.search.results;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.model.VenueFilterOptions;
import com.ibrahimekinci.barcrowd.domain.usecase.SearchVenuesUseCase;

import java.util.List;

public class ResultsViewModel extends ViewModel {

    private final SearchVenuesUseCase searchVenuesUseCase;
    private final MutableLiveData<VenueFilterOptions> filterInput = new MutableLiveData<>();
    public final LiveData<List<Venue>> searchResults;

    public ResultsViewModel(SearchVenuesUseCase searchVenuesUseCase) {
        this.searchVenuesUseCase = searchVenuesUseCase;

        // Initialize here after assignment
        this.searchResults = Transformations.switchMap(filterInput,
                filter -> this.searchVenuesUseCase.execute(filter));
    }

    public void search(VenueFilterOptions filters) {
        filterInput.setValue(filters);
    }
}