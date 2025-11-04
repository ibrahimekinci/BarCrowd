package com.ibrahimekinci.barcrowd.domain.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Entity (POJO) for the /Users collection.
 * Implements Parcelable to be passed between Android components.
 */
public class User implements Parcelable {

    @DocumentId
    private String userId;

    @NonNull
    private String fullName;

    @NonNull
    private String username;

    @NonNull
    private String email;

    private String profilePhotoUrl; // Not required, default is null

    private boolean isTrusted = false; // Default value from schema
    private boolean isDeleted = false; // Default value from schema
    private Timestamp deletedAt = null; // Default value from schema

    @ServerTimestamp
    private Timestamp updatedAt;

    @ServerTimestamp
    private Timestamp createdAt;

    // Public, no-argument constructor is required for Firestore
    public User() {}

    // --- Parcelable Implementation ---

    protected User(Parcel in) {
        userId = in.readString();
        fullName = in.readString();
        username = in.readString();
        email = in.readString();
        profilePhotoUrl = in.readString();
        isTrusted = in.readByte() != 0;
        isDeleted = in.readByte() != 0;
        deletedAt = in.readParcelable(Timestamp.class.getClassLoader());
        updatedAt = in.readParcelable(Timestamp.class.getClassLoader());
        createdAt = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(fullName);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(profilePhotoUrl);
        dest.writeByte((byte) (isTrusted ? 1 : 0));
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeParcelable(deletedAt, flags);
        dest.writeParcelable(updatedAt, flags);
        dest.writeParcelable(createdAt, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // --- Getters ---

    public String getUserId() { return userId; }
    @NonNull public String getFullName() { return fullName; }
    @NonNull public String getUsername() { return username; }
    @NonNull public String getEmail() { return email; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public boolean isTrusted() { return isTrusted; }
    public boolean isDeleted() { return isDeleted; }
    public Timestamp getDeletedAt() { return deletedAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public Timestamp getCreatedAt() { return createdAt; }

    // --- Setters ---

    public void setUserId(String userId) { this.userId = userId; }
    public void setFullName(@NonNull String fullName) { this.fullName = fullName; }
    public void setUsername(@NonNull String username) { this.username = username; }
    public void setEmail(@NonNull String email) { this.email = email; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
    public void setTrusted(boolean trusted) { isTrusted = trusted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public void setDeletedAt(Timestamp deletedAt) { this.deletedAt = deletedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}