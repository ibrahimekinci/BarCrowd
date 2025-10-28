package com.ibrahimekinci.barcrowd.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    private String id; // Now explicitly non-null
    private String email;
    private long createdAt;

    // Constructors, getters/setters as before
    public UserEntity() {}
    // Parameterized constructor (mark as @Ignore)
    @Ignore
    public UserEntity(@NonNull String id, String email, long createdAt) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters and setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}