package com.ibrahimekinci.barcrowd.ui.liveupdate.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.usecase.GetAllLiveUpdatesUseCase;

import java.util.List;
import java.util.stream.Collectors;

public class AllLiveUpdatesViewModel extends ViewModel {

    private final GetAllLiveUpdatesUseCase getAllLiveUpdatesUseCase;

    // LiveData'yı filtreleyip UI'a gönderecek olan ana LiveData
    private final MediatorLiveData<List<LiveUpdate>> liveFeedUpdates = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    public AllLiveUpdatesViewModel(GetAllLiveUpdatesUseCase getAllLiveUpdatesUseCase) {
        this.getAllLiveUpdatesUseCase = getAllLiveUpdatesUseCase;

        loadUpdates();
    }

    private void loadUpdates() {
        isLoading.setValue(true);

        // UseCase'den gelen LiveData'yı dinle
        LiveData<List<LiveUpdate>> source = getAllLiveUpdatesUseCase.execute();

        // MediatorLiveData kullanarak veriyi al ve filtrele
        liveFeedUpdates.addSource(source, updates -> {
            if (updates != null) {
                // Sadece silinmemiş ve geçerli olanları filtrele
                List<LiveUpdate> filtered = updates.stream()
                        .filter(update -> !update.getIsDeleted())
                        .collect(Collectors.toList());

                liveFeedUpdates.setValue(filtered);
                isLoading.setValue(false);
            }
        });
    }

    public LiveData<List<LiveUpdate>> getLiveFeedUpdates() {
        return liveFeedUpdates;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}