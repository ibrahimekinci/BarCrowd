package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper.AuthCallback;

public interface UserRepository {
    void insertUser(User user);
    LiveData<User> getUserById(String userId);
    User getUserByEmail(String email);
    User getCurrentUser(); // Token-validated cache
    void signUp(String email, String password, AuthCallback callback); // Online + cache insert
    void signIn(String email, String password, AuthCallback callback); // Online + cache refresh
    void syncUsers();
}