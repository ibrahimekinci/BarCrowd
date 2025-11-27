package com.ibrahimekinci.barcrowd.ui.search.results;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.ibrahimekinci.barcrowd.domain.usecase.SearchVenuesUseCase;

public class ResultsViewModelFactory implements ViewModelProvider.Factory {
    private final SearchVenuesUseCase useCase;

    public ResultsViewModelFactory(SearchVenuesUseCase useCase) {
        this.useCase = useCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ResultsViewModel(useCase);
    }
}