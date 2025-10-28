package com.ibrahimekinci.barcrowd.di;

import androidx.room.Room;

import com.ibrahimekinci.barcrowd.BarCrowdApplication; // Custom Application class
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.remote.FirebaseAuthWrapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepositoryImpl;
import com.ibrahimekinci.barcrowd.data.repository.UserRepository;
import com.ibrahimekinci.barcrowd.data.repository.UserRepositoryImpl;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepositoryImpl;
import com.ibrahimekinci.barcrowd.domain.usecase.*;
import com.ibrahimekinci.barcrowd.util.AppLogger;

/**
 * Manual dependency injector for providing repositories and use cases.
 */
public class DependencyInjector {
    private static AppDatabase db;
    private static FirestoreWrapper firestore;
    private static FirebaseAuthWrapper authWrapper;
    private static BarCrowdApplication app; // Stored for context injection

    private static RoomDatabaseProvider dbProvider = new RoomDatabaseProvider.ProductionProvider();
    // Production init (call in Application.onCreate)
// Production init (call in Application.onCreate)
    // ADDED dbProvider parameter
    public static void init(BarCrowdApplication app) {
        DependencyInjector.app = app;
        db = dbProvider.getDatabase(app); // FIX: Use provider instead of static Room call
        firestore = new FirestoreWrapper(); //
        authWrapper = new FirebaseAuthWrapper(); //
        AppLogger.i("Dependencies initialized"); //
    }

    public static void setDbProvider(RoomDatabaseProvider provider) {
        dbProvider = provider;
    }

    public static BarCrowdApplication getApp() {
        return app;
    }

    // Factory methods
    public UserRepository getUserRepository() {
        return new UserRepositoryImpl(db, firestore, authWrapper);
    }

    public VenueRepository getVenueRepository() {
        return new VenueRepositoryImpl(db, firestore);
    }

    public LiveUpdateRepository getLiveUpdateRepository() {
        return new LiveUpdateRepositoryImpl(db, firestore, getApp());
    }

    // Use case factories
    public SignUpUseCase getSignUpUseCase() {
        return new SignUpUseCase(getUserRepository());
    }

    public SignInUseCase getSignInUseCase() {
        return new SignInUseCase(getUserRepository());
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
        return new PostLiveUpdateUseCase(getLiveUpdateRepository());
    }

    public GetUpdatesForVenueUseCase getGetUpdatesForVenueUseCase() {
        return new GetUpdatesForVenueUseCase(getLiveUpdateRepository());
    }

    public GetMyContributionsUseCase getGetMyContributionsUseCase() {
        return new GetMyContributionsUseCase(getLiveUpdateRepository());
    }

    public GetCurrentUserUseCase getGetCurrentUserUseCase() {
        return new GetCurrentUserUseCase(getUserRepository());
    }
}