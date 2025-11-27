package com.ibrahimekinci.barcrowd.di;

import android.app.Application;

import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.data.remote.StorageWrapper;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepositoryImpl;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.data.repository.UserRepositoryImpl;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepositoryImpl;
import com.ibrahimekinci.barcrowd.domain.usecase.GetAllLiveUpdatesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetAllVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetCurrentUserUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetHomePageVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetMyContributionsUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetRecentLiveUpdatesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetUpdatesForVenueUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.GetVenueByIdUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.IsUserLoggedInUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.PostLiveUpdateUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SearchVenuesUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SignInUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SignOutUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SignUpUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SoftDeleteLiveUpdateUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.SyncHomeDataUseCase;
import com.ibrahimekinci.barcrowd.domain.usecase.UpdateUserUseCase;
import com.ibrahimekinci.barcrowd.util.AppLogger;

public class DependencyInjector {

    private static AppDatabase db;
    private static FirestoreWrapper firestore;
    private static FirebaseAuthWrapper authWrapper;
    private static StorageWrapper storageWrapper; // Added
    private static BarCrowdApplication app;

    private static RoomDatabaseProvider dbProvider = new RoomDatabaseProvider.ProductionProvider();

    public static void init(BarCrowdApplication app) {
        DependencyInjector.app = app;
        db = dbProvider.getDatabase(app);
        firestore = new FirestoreWrapper();
        authWrapper = new FirebaseAuthWrapper();
        storageWrapper = new StorageWrapper(); // Initialized
        AppLogger.i("Dependencies initialized");
    }

    public static void setDbProvider(RoomDatabaseProvider provider) {
        dbProvider = provider;
    }

    public static Application getApp() {
        return app;
    }

    public StorageWrapper getStorageWrapper() {
        return storageWrapper;
    }

    public void checkAndSeedData() {
        // Initializes the seeder with the Firestore wrapper
        com.ibrahimekinci.barcrowd.debug.SampleDataSeeder seeder =
                new com.ibrahimekinci.barcrowd.debug.SampleDataSeeder(firestore);
        seeder.seedData();
    }
    public UserRepository getUserRepository() {
        return new UserRepositoryImpl(firestore, authWrapper);
    }

    public VenueRepository getVenueRepository() {
        return new VenueRepositoryImpl(db, firestore);
    }

    public LiveUpdateRepository getLiveUpdateRepository() {
        return new LiveUpdateRepositoryImpl(db, firestore, getApp());
    }

    public SoftDeleteLiveUpdateUseCase getSoftDeleteLiveUpdateUseCase() {
        return new SoftDeleteLiveUpdateUseCase(getLiveUpdateRepository());
    }

    public SignUpUseCase getSignUpUseCase() {
        return new SignUpUseCase(getUserRepository());
    }

    public SignInUseCase getSignInUseCase() {
        return new SignInUseCase(getUserRepository());
    }

    public SignOutUseCase getSignOutUseCase() {
        return new SignOutUseCase(getUserRepository());
    }

    public IsUserLoggedInUseCase getIsUserLoggedInUseCase() {
        return new IsUserLoggedInUseCase(getUserRepository());
    }

    public GetCurrentUserUseCase getGetCurrentUserUseCase() {
        return new GetCurrentUserUseCase(getUserRepository());
    }

    public UpdateUserUseCase getUpdateUserUseCase() {
        return new UpdateUserUseCase(getUserRepository());
    }

    public GetHomePageVenuesUseCase getGetHomePageVenuesUseCase() {
        return new GetHomePageVenuesUseCase(getVenueRepository());
    }

    public GetAllVenuesUseCase getGetAllVenuesUseCase() {
        return new GetAllVenuesUseCase(getVenueRepository());
    }

    public GetVenueByIdUseCase getGetVenueByIdUseCase() {
        return new GetVenueByIdUseCase(getVenueRepository());
    }

    public SearchVenuesUseCase getSearchVenuesUseCase() {
        return new SearchVenuesUseCase(getVenueRepository());
    }

    public PostLiveUpdateUseCase getPostLiveUpdateUseCase() {
        return new PostLiveUpdateUseCase(getLiveUpdateRepository(), getVenueRepository());
    }

    public GetUpdatesForVenueUseCase getGetUpdatesForVenueUseCase() {
        return new GetUpdatesForVenueUseCase(getLiveUpdateRepository());
    }

    public GetRecentLiveUpdatesUseCase getGetRecentLiveUpdatesUseCase() {
        return new GetRecentLiveUpdatesUseCase(getLiveUpdateRepository());
    }

    public GetAllLiveUpdatesUseCase getGetAllLiveUpdatesUseCase() {
        return new GetAllLiveUpdatesUseCase(getLiveUpdateRepository());
    }

    public SyncHomeDataUseCase getSyncHomeDataUseCase() {
        return new SyncHomeDataUseCase(getVenueRepository(), getLiveUpdateRepository());
    }

}