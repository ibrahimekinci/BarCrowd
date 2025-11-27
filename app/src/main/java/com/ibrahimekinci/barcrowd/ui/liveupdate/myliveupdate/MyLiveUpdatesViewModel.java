package com.ibrahimekinci.barcrowd.ui.liveupdate.myliveupdate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SoftDeleteLiveUpdateUseCase;

import java.util.ArrayList;
import java.util.List;

public class MyLiveUpdatesViewModel extends ViewModel {

    private final LiveUpdateRepository repository;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase;

    private final MediatorLiveData<List<LiveUpdate>> myUpdates = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> deleteMessage = new MutableLiveData<>();

    private LiveData<User> userLiveData;
    private LiveData<List<LiveUpdate>> repoLiveData;

    public MyLiveUpdatesViewModel(LiveUpdateRepository repository,
                                  GetCurrentUserUseCase getCurrentUserUseCase,
                                  SoftDeleteLiveUpdateUseCase softDeleteLiveUpdateUseCase) {
        this.repository = repository;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.softDeleteLiveUpdateUseCase = softDeleteLiveUpdateUseCase;

        loadMyUpdates();
    }

    public void loadMyUpdates() {
        isLoading.setValue(true);

        // 1. Get User
        userLiveData = getCurrentUserUseCase.execute();

        // 2. Observe User
        myUpdates.addSource(userLiveData, user -> {
            if (user == null) {
                error.setValue("User not logged in");
                isLoading.setValue(false);
                return;
            }

            if (repoLiveData != null) myUpdates.removeSource(repoLiveData);

            // Trigger Sync
            repository.syncRecentLiveUpdates();

            // Observe Repo LiveData
            repoLiveData = repository.getAllLiveUpdates();
            myUpdates.addSource(repoLiveData, allUpdates -> {
                List<LiveUpdate> filtered = new ArrayList<>();
                if (allUpdates != null) {
                    for (LiveUpdate u : allUpdates) {
                        if (u.getUserId() != null && u.getUserId().equals(user.getUserId()) && !u.getIsDeleted()) {
                            filtered.add(u);
                        }
                    }
                }
                myUpdates.setValue(filtered);
                isLoading.setValue(false);
            });
        });
    }

    public void deleteUpdate(LiveUpdate update) {
        isLoading.setValue(true);
        update.setDeleted(true);

        // FIX: Use LiveUpdateRepository.DeleteCallback
        softDeleteLiveUpdateUseCase.execute(update, new LiveUpdateRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                deleteMessage.postValue("Update deleted.");
                isLoading.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                error.postValue("Delete failed: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public LiveData<List<LiveUpdate>> getMyUpdates() { return myUpdates; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getDeleteMessage() { return deleteMessage; }
}