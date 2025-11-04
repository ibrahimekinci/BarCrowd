package com.ibrahimekinci.barcrowd.data.remote;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.ibrahimekinci.barcrowd.util.StorageException;

public class StorageWrapper {
    private static final String PROFILE_PHOTO_STORAGE_PATH = "profile_photos/";
    private static final String VIDEO_STORAGE_PATH = "videos/";
    private static final long MAX_PROFILE_PHOTO_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_VIDEO_SIZE_BYTES = 100 * 1024 * 1024; // 100 MB

    private final StorageReference storage;
    private final Context context;

    public StorageWrapper() {
        this.storage = FirebaseStorage.getInstance().getReference();
        // Get context from the Application class via the DI
        this.context = DependencyInjector.getApp().getApplicationContext();
    }

    public void uploadProfilePhoto(Uri fileUri, String userId, Callback<String> callback) {
        long fileSize = getFileSize(fileUri);
        if (fileSize == -1) {
            callback.onFailure(new StorageException("Could not determine file size.", null));
            return;
        }
        if (fileSize > MAX_PROFILE_PHOTO_SIZE_BYTES) {
            callback.onFailure(new StorageException("Profile photo exceeds 5 MB limit", null));
            return;
        }
        String path = PROFILE_PHOTO_STORAGE_PATH + userId + ".jpg";
        uploadMedia(fileUri, path, callback);
    }

    public void uploadVideo(Uri fileUri, String videoName, Callback<String> callback) {
        long fileSize = getFileSize(fileUri);
        if (fileSize == -1) {
            callback.onFailure(new StorageException("Could not determine file size.", null));
            return;
        }
        if (fileSize > MAX_VIDEO_SIZE_BYTES) {
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

    private long getFileSize(Uri fileUri) {
        try (Cursor cursor = context.getContentResolver().query(fileUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    return cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception e) {
            AppLogger.e("Failed to get file size from ContentResolver", e);
        }
        return -1; // Indicates failure
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(StorageException e);
    }
}