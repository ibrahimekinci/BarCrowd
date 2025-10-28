package com.ibrahimekinci.barcrowd.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.UserDao;
import com.ibrahimekinci.barcrowd.data.mapper.UserMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.User;
import com.ibrahimekinci.barcrowd.util.AppLogger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import java.util.concurrent.Executors;

public class UserRepositoryImpl implements UserRepository {
    private UserDao dao;
    private FirestoreWrapper firestore;
    private FirebaseAuthWrapper authWrapper;

    public UserRepositoryImpl(AppDatabase db, FirestoreWrapper firestore, FirebaseAuthWrapper authWrapper) {
        this.dao = db.userDao();
        this.firestore = firestore;
        this.authWrapper = authWrapper;
    }

    @Override
    public void insertUser(User user) {
        dao.insert(UserMapper.toEntity(user));
        AppLogger.d("Local user cached: " + user.getId());
    }

    @Override
    public LiveData<User> getUserById(String userId) {
        syncUsers();
        return Transformations.map(dao.getUserById(userId), UserMapper::toModel);
    }

    @Override
    public User getUserByEmail(String email) {
        return UserMapper.toModel(dao.getUserByEmail(email));
    }

    @Override
    public User getCurrentUser() {
        FirebaseUser firebaseUser = authWrapper.getCurrentUser();
        if (firebaseUser != null) {
            User localUser = getUserByEmail(firebaseUser.getEmail());
            if (localUser == null) {
                localUser = new User(firebaseUser.getUid(), firebaseUser.getEmail(), System.currentTimeMillis());
                insertUser(localUser);
                AppLogger.d("Cached current user: " + firebaseUser.getUid());
            }
            return localUser;
        }
        AppLogger.w("Token invalid; force login");
        return null;
    }

    @Override
    public void signUp(String email, String password, FirebaseAuthWrapper.AuthCallback callback) {
        authWrapper.signUp(email, password, new FirebaseAuthWrapper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                User user = new User(firebaseUser.getUid(), email, System.currentTimeMillis());
                insertUser(user);
                AppLogger.i("Sign-up cached: " + firebaseUser.getUid());
                callback.onSuccess(firebaseUser);
            }

            @Override
            public void onFailure(Exception e) {
                AppLogger.e("Sign-up failed", e);
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void signIn(String email, String password, FirebaseAuthWrapper.AuthCallback callback) {
        authWrapper.signIn(email, password, new FirebaseAuthWrapper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                User user = getUserByEmail(email);
                if (user == null || !user.getId().equals(firebaseUser.getUid())) {
                    user = new User(firebaseUser.getUid(), email, System.currentTimeMillis());
                    insertUser(user);
                    AppLogger.i("Sign-in cached: " + firebaseUser.getUid());
                }
                callback.onSuccess(firebaseUser);
            }

            @Override
            public void onFailure(Exception e) {
                AppLogger.e("Sign-in failed", e);
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void syncUsers() {
        firestore.listenForChanges("users", null, null, snapshots -> {  // Updated to use wrapper with null field
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                    User user = dc.getDocument().toObject(User.class);
                    if (user != null) {
                        insertUser(user);
                        AppLogger.d("Synced user: " + user.getId() + " (email: " + user.getEmail() + ")");
                    }
                }
            }
        });
    }
}