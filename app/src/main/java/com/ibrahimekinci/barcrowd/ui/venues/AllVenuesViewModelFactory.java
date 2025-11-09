package com.ibrahimekinci.barcrowd.ui.venues;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.ibrahimekinci.barcrowd.domain.usecase.GetAllVenuesUseCase;

/**
 * Factory for creating AllVenuesViewModel instances with required dependencies.
 */
public class AllVenuesViewModelFactory implements ViewModelProvider.Factory {

    private final GetAllVenuesUseCase getAllVenuesUseCase;

    public AllVenuesViewModelFactory(GetAllVenuesUseCase getAllVenuesUseCase) {
        this.getAllVenuesUseCase = getAllVenuesUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AllVenuesViewModel.class)) {
            // Create and return an instance of AllVenuesViewModel
            return (T) new AllVenuesViewModel(getAllVenuesUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}