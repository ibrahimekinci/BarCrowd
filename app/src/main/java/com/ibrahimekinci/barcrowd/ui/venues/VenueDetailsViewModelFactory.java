package com.ibrahimekinci.barcrowd.ui.venues;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ibrahimekinci.barcrowd.domain.usecase.GetUpdatesForVenueUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetVenueByIdUseCase;

public class VenueDetailsViewModelFactory implements ViewModelProvider.Factory {

    private final GetVenueByIdUseCase getVenueByIdUseCase;
    private final GetUpdatesForVenueUseCase getUpdatesForVenueUseCase;

    public VenueDetailsViewModelFactory(GetVenueByIdUseCase getVenueByIdUseCase, GetUpdatesForVenueUseCase getUpdatesForVenueUseCase) {
        this.getVenueByIdUseCase = getVenueByIdUseCase;
        this.getUpdatesForVenueUseCase = getUpdatesForVenueUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(VenueDetailsViewModel.class)) {
            return (T) new VenueDetailsViewModel(getVenueByIdUseCase, getUpdatesForVenueUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}