package com.ibrahimekinci.barcrowd.ui.liveupdate.post;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ibrahimekinci.barcrowd.data.remote.StorageWrapper;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.domain.usecase.GetAllVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.PostLiveUpdateUseCase;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.StorageException;
import com.ibrahimekinci.barcrowd.util.ValidationException;

import java.util.List;

public class NewLiveUpdateViewModel extends ViewModel {

    private final GetAllVenuesUseCase getAllVenuesUseCase;
    private final PostLiveUpdateUseCase postLiveUpdateUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final StorageWrapper storageWrapper;

    private final LiveData<List<Venue>> allVenues;
    private final LiveData<User> currentUser;

    // UI State
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> loadingMessage = new MutableLiveData<>("");
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postSuccess = new MutableLiveData<>();

    public NewLiveUpdateViewModel(GetAllVenuesUseCase getAllVenuesUseCase,
                                  PostLiveUpdateUseCase postLiveUpdateUseCase,
                                  GetCurrentUserUseCase getCurrentUserUseCase,
                                  StorageWrapper storageWrapper) {
        this.getAllVenuesUseCase = getAllVenuesUseCase;
        this.postLiveUpdateUseCase = postLiveUpdateUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.storageWrapper = storageWrapper;

        this.allVenues = getAllVenuesUseCase.execute();
        this.currentUser = getCurrentUserUseCase.execute();
    }

    public LiveData<List<Venue>> getAllVenues() { return allVenues; }
    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getLoadingMessage() { return loadingMessage; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getPostSuccess() { return postSuccess; }

    public void submitUpdate(String venueId, Uri videoUri, Uri thumbnailUri, String crowdLevel, String waitTime, String ageRange, String description) {
        User user = currentUser.getValue();

        if (user == null) {
            error.setValue("User not logged in.");
            return;
        }
        if (venueId == null || venueId.isEmpty()) {
            error.setValue("Please select a venue.");
            return;
        }
        if (videoUri == null) {
            error.setValue("Please record or select a video.");
            return;
        }
        if (thumbnailUri == null) {
            error.setValue("Thumbnail generation failed.");
            return;
        }

        isLoading.setValue(true);
        long timestamp = System.currentTimeMillis();
        String thumbName = "thumb_" + user.getUserId() + "_" + timestamp + ".jpg";
        String videoName = "video_" + user.getUserId() + "_" + timestamp + ".mp4";

        loadingMessage.setValue("Uploading thumbnail...");

        storageWrapper.uploadThumbnail(thumbnailUri, thumbName, new StorageWrapper.Callback<String>() {
            @Override
            public void onSuccess(String thumbnailUrl) {
                loadingMessage.setValue("Uploading video...");
                uploadVideoAndPost(user.getUserId(), venueId, videoUri, videoName, thumbnailUrl, crowdLevel, waitTime, ageRange, description);
            }

            @Override
            public void onFailure(StorageException e) {
                isLoading.postValue(false);
                error.postValue("Thumbnail upload failed: " + e.getMessage());
            }
        });
    }

    private void uploadVideoAndPost(String userId, String venueId, Uri videoUri, String videoName, String thumbnailUrl,
                                    String crowdLevel, String waitTime, String ageRange, String description) {

        storageWrapper.uploadVideo(videoUri, videoName, new StorageWrapper.Callback<String>() {
            @Override
            public void onSuccess(String videoUrl) {
                loadingMessage.setValue("Finalizing post...");

                LiveUpdate update = new LiveUpdate();
                update.setVenueId(venueId);
                update.setUserId(userId);
                update.setMediaUrl(videoUrl);
                update.setThumbnailUrl(thumbnailUrl);
                update.setCrowdLevel(crowdLevel);
                update.setWaitTime(waitTime);
                update.setAgeRange(ageRange);
                update.setDescription(description);

                savePostToFirestore(update);
            }

            @Override
            public void onFailure(StorageException e) {
                isLoading.postValue(false);
                error.postValue("Video upload failed: " + e.getMessage());
            }
        });
    }

    private void savePostToFirestore(LiveUpdate update) {
        // FIXED: Provide the required Callback to execute()
        postLiveUpdateUseCase.execute(update, new PostLiveUpdateUseCase.Callback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                postSuccess.postValue(true);
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.postValue(false);
                if (e instanceof ValidationException) {
                    error.postValue(e.getMessage());
                } else {
                    error.postValue("Failed to save post: " + e.getMessage());
                    AppLogger.e("Firestore Error", e);
                }
            }
        });
    }
}