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
import java.util.UUID;

public class NewLiveUpdateViewModel extends ViewModel {

    private final GetAllVenuesUseCase getAllVenuesUseCase;
    private final PostLiveUpdateUseCase postLiveUpdateUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final StorageWrapper storageWrapper;

    private final LiveData<List<Venue>> allVenues;
    private final LiveData<User> currentUser;

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

        // Seçilen mekanı bul (Bilgilerini kopyalamak için)
        Venue selectedVenue = findVenueById(venueId);
        if (selectedVenue == null) {
            error.setValue("Selected venue not found in list.");
            return;
        }

        isLoading.setValue(true);
        String updateUUID = UUID.randomUUID().toString();

        long timestamp = System.currentTimeMillis();
        String thumbName = "thumb_" + user.getUserId() + "_" + timestamp + ".jpg";
        String videoName = "video_" + user.getUserId() + "_" + timestamp + ".mp4";

        loadingMessage.setValue("Uploading thumbnail...");

        storageWrapper.uploadThumbnail(thumbnailUri, thumbName, new StorageWrapper.Callback<String>() {
            @Override
            public void onSuccess(String thumbnailUrl) {
                loadingMessage.setValue("Uploading video...");
                uploadVideoAndPost(updateUUID, user, selectedVenue, videoUri, videoName, thumbnailUrl, crowdLevel, waitTime, ageRange, description);
            }

            @Override
            public void onFailure(StorageException e) {
                isLoading.postValue(false);
                error.postValue("Thumbnail upload failed: " + e.getMessage());
            }
        });
    }

    // Yardımcı metod: ID'den Venue nesnesini bulur
    private Venue findVenueById(String venueId) {
        List<Venue> venues = allVenues.getValue();
        if (venues != null) {
            for (Venue v : venues) {
                if (v.getVenueId().equals(venueId)) {
                    return v;
                }
            }
        }
        return null;
    }

    private void uploadVideoAndPost(String updateId, User user, Venue venue, Uri videoUri, String videoName, String thumbnailUrl,
                                    String crowdLevel, String waitTime, String ageRange, String description) {

        storageWrapper.uploadVideo(videoUri, videoName, new StorageWrapper.Callback<String>() {
            @Override
            public void onSuccess(String videoUrl) {
                loadingMessage.setValue("Finalizing post...");

                LiveUpdate update = new LiveUpdate();
                update.setUpdateId(updateId);

                // İlişkisel ID'ler
                update.setVenueId(venue.getVenueId());
                update.setUserId(user.getUserId());

                // --- DÜZELTİLEN KISIM: Denormalize Veriler ---
                // Mekan bilgileri (Feed'de görünmesi için)
                update.setVenueName(venue.getName());
                update.setVenueType(venue.getType());
                update.setVenueLogoUrl(venue.getLogoUrl());

                // Kullanıcı bilgileri
                update.setUserName(user.getUsername());
                update.setUserPhotoUrl(user.getProfilePhotoUrl());
                // --------------------------------------------

                update.setMediaUrl(videoUrl);
                update.setThumbnailUrl(thumbnailUrl);
                update.setCrowdLevel(crowdLevel);
                update.setWaitTime(waitTime);
                update.setAgeRange(ageRange);
                update.setDescription(description);

                update.setDeleted(false);
                update.setCreatedAt(com.google.firebase.Timestamp.now());

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