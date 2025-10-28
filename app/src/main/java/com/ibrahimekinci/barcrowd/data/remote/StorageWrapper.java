package com.ibrahimekinci.barcrowd.data.remote;

import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.StorageException;

public class StorageWrapper {
    private static final String PROFILE_PHOTO_STORAGE_PATH = "profile_photos/";
    private static final String VIDEO_STORAGE_PATH = "videos/";
    private static final long MAX_PROFILE_PHOTO_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_VIDEO_SIZE_BYTES = 100 * 1024 * 1024; // 100 MB (adjust as needed)
    private StorageReference storage = FirebaseStorage.getInstance().getReference();

    /**
     * Uploads a profile photo with size validation.
     * @param fileUri URI of the photo file.
     * @param userId ID of the user to name the file (e.g., "profile_photos/user1.jpg").
     * @param callback Callback with the download URL or error.
     */
    public void uploadProfilePhoto(Uri fileUri, String userId, Callback<String> callback) {
        if (getFileSize(fileUri) > MAX_PROFILE_PHOTO_SIZE_BYTES) {
            callback.onFailure(new StorageException("Profile photo exceeds 5 MB limit", null));
            return;
        }
        String path = PROFILE_PHOTO_STORAGE_PATH + userId + ".jpg";
        uploadMedia(fileUri, path, callback);
    }

    /**
     * Uploads a video with progress tracking.
     * @param fileUri URI of the video file.
     * @param videoName Name of the video (e.g., "liveupdate01.mp4").
     * @param callback Callback with the download URL or error.
     */
    public void uploadVideo(Uri fileUri, String videoName, Callback<String> callback) {
        if (getFileSize(fileUri) > MAX_VIDEO_SIZE_BYTES) {
            callback.onFailure(new StorageException("Video exceeds 100 MB limit", null));
            return;
        }
        String path = VIDEO_STORAGE_PATH + videoName;
        StorageReference ref = storage.child(path);
        UploadTask uploadTask = ref.putFile(fileUri);

        uploadTask.addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    AppLogger.i("Upload progress: " + progress + "%");
                }).addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            AppLogger.i("Video uploaded: " + uri.toString());
                            callback.onSuccess(uri.toString());
                        })
                        .addOnFailureListener(e -> {
                            AppLogger.e("Download URL fetch failed", e);
                            callback.onFailure(new StorageException("URL fetch failed: " + e.getMessage(), e));
                        }))
                .addOnFailureListener(e -> {
                    AppLogger.e("Video upload failed", e);
                    callback.onFailure(new StorageException("Upload failed: " + e.getMessage(), e));
                });
    }

    /**
     * Generic media upload method (for internal use or venue photos if needed).
     * @param fileUri URI of the media file.
     * @param path Storage path (e.g., "photos/venue1.jpg").
     * @param callback Callback with the download URL or error.
     */
    private void uploadMedia(Uri fileUri, String path, Callback<String> callback) {
        StorageReference ref = storage.child(path);
        ref.putFile(fileUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            AppLogger.i("Media uploaded: " + uri.toString());
                            callback.onSuccess(uri.toString());
                        })
                        .addOnFailureListener(e -> {
                            AppLogger.e("Download URL fetch failed", e);
                            callback.onFailure(new StorageException("URL fetch failed: " + e.getMessage(), e));
                        }))
                .addOnFailureListener(e -> {
                    AppLogger.e("Media upload failed", e);
                    callback.onFailure(new StorageException("Upload failed: " + e.getMessage(), e));
                });
    }

    /**
     * Retrieves the size of the file at the given URI.
     * @param fileUri URI of the file.
     * @return Size in bytes, or -1 if unavailable.
     */
    private long getFileSize(Uri fileUri) {
        try {
            return new java.io.File(fileUri.getPath()).length();
        } catch (Exception e) {
            AppLogger.e("Failed to get file size", e);
            return -1;
        }
    }

    /**
     * Callback interface for asynchronous operations.
     */
    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(StorageException e);
    }
}