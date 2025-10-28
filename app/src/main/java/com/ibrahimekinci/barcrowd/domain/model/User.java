package com.ibrahimekinci.barcrowd.domain.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private long createdAt;

    public User() {}

    public User(String id, String email, long createdAt) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Business method example
    public boolean isValid() {
        return email != null && !email.isEmpty();
    }
}