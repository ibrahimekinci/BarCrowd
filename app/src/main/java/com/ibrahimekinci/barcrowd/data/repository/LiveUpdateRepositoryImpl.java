package com.ibrahimekinci.barcrowd.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.ibrahimekinci.barcrowd.data.local.AppDatabase;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateDao;
import com.ibrahimekinci.barcrowd.data.local.LiveUpdateEntity;
import com.ibrahimekinci.barcrowd.data.mapper.LiveUpdateMapper;
import com.ibrahimekinci.barcrowd.data.remote.FirestoreWrapper;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LiveUpdateRepositoryImpl implements LiveUpdateRepository {

    private final LiveUpdateDao dao;
    private final FirestoreWrapper firestore;
    private final Application app;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Canlı dinleyiciyi takip etmek için değişken (Memory leak ve duplicate önler)
    private ListenerRegistration liveUpdatesListener;

    public LiveUpdateRepositoryImpl(AppDatabase db, FirestoreWrapper firestore, Application app) {
        this.dao = db.liveUpdateDao();
        this.firestore = firestore;
        this.app = app;
    }

    // --- OTOMATİK SENKRONİZASYON (ANA ÇÖZÜM) ---

    @Override
    public void syncRecentLiveUpdates() {
        // Eğer zaten bir dinleyici varsa tekrar oluşturma (Duplicate önleme)
        if (liveUpdatesListener != null) {
            AppLogger.d("LiveUpdates listener already active. Skipping re-init.");
            return;
        }

        AppLogger.i("Starting Real-time LiveUpdates Listener...");

        Query query = firestore.getDb().collection("LiveUpdates")
                .whereEqualTo("isDeleted", false)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100);

        // Listener'ı başlatıyoruz ve referansını değişkene atıyoruz
        liveUpdatesListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                AppLogger.e("Sync listener failed", e);
                return;
            }

            if (snapshots != null && !snapshots.isEmpty()) {
                AppLogger.d("🔥 Firestore Event Received! Changes: " + snapshots.getDocumentChanges().size());

                executor.execute(() -> {
                    int addedCount = 0;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        // Sadece EKLENEN ve DEĞİŞENLERİ (Modified) alıyoruz
                        if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            LiveUpdate model = dc.getDocument().toObject(LiveUpdate.class);
                            LiveUpdateEntity entity = LiveUpdateMapper.toEntity(model);

                            if (entity != null) {
                                entity.setSyncStatus(true);
                                // Room'a yazınca LiveData Home'da otomatik tetiklenir
                                dao.insert(entity);
                                addedCount++;
                            }
                        }
                        // Silinenleri de yerel DB'den silebiliriz (Optional)
                        if (dc.getType() == DocumentChange.Type.REMOVED) {
                            LiveUpdate model = dc.getDocument().toObject(LiveUpdate.class);
                            // dao.deleteById(model.getUpdateId()); gibi bir metod varsa kullanılabilir
                        }
                    }
                    AppLogger.d("✅ Processed " + addedCount + " updates into Local DB via Listener.");
                });
            }
        });
    }

    // --- YENİ POST ATMA İŞLEMİ ---

    @Override
    public void saveLiveUpdate(LiveUpdate liveUpdate, SaveCallback callback) {
        if (liveUpdate.getUpdateId() == null) {
            if (callback != null) callback.onError(new Exception("Update ID is null"));
            return;
        }

        firestore.getDb().collection("LiveUpdates")
                .document(liveUpdate.getUpdateId())
                .set(liveUpdate)
                .addOnSuccessListener(aVoid -> {
                    AppLogger.d("Post sent to Firestore successfully: " + liveUpdate.getUpdateId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    AppLogger.e("Failed to send post", e);
                    if (callback != null) callback.onError(e);
                });
    }

    @Override
    public LiveData<List<LiveUpdate>> getAllLiveUpdates() {
        return Transformations.map(dao.getAllLiveUpdates(), entities ->
                entities.stream()
                        .map(LiveUpdateMapper::toModel)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void softDeleteUpdate(LiveUpdate update, DeleteCallback callback) {
        if (update.getUpdateId() == null) return;
        firestore.getDb().collection("LiveUpdates")
                .document(update.getUpdateId())
                .update("isDeleted", true)
                .addOnSuccessListener(aVoid -> {
                    executor.execute(() -> {
                        update.setDeleted(true);
                        LiveUpdateEntity entity = LiveUpdateMapper.toEntity(update);
                        if (entity != null) dao.insert(entity);
                    });
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    @Override
    public void getAllLiveUpdates(LoadCallback callback) {
        // Bu metod callback tabanlı veri çekmek içindir (Manuel yenileme vs.)
        firestore.getDb().collection("LiveUpdates")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<LiveUpdate> list = new ArrayList<>();
                    if (snapshots != null) {
                        list = snapshots.toObjects(LiveUpdate.class);
                        saveListToLocal(list);
                    }
                    callback.onLoaded(list);
                })
                .addOnFailureListener(callback::onError);
    }

    // Boilerplate / Empty methods to prevent compile errors
    @Override public void postUpdate(LiveUpdate update) { saveLiveUpdate(update, null); }
    @Override public LiveData<List<LiveUpdate>> getRecentLiveUpdates() { return getAllLiveUpdates(); }
    @Override public LiveData<List<LiveUpdate>> getUpdatesForVenue(String venueId) { return new MutableLiveData<>(new ArrayList<>()); }
    @Override public LiveData<List<LiveUpdate>> getUserContributions(String userId) { return new MutableLiveData<>(new ArrayList<>()); }
    @Override public void syncUpdates(String venueId) {}
    @Override public void syncPending() {}

    private void saveListToLocal(List<LiveUpdate> list) {
        executor.execute(() -> {
            for (LiveUpdate item : list) {
                LiveUpdateEntity entity = LiveUpdateMapper.toEntity(item);
                if (entity != null) {
                    entity.setSyncStatus(true);
                    dao.insert(entity);
                }
            }
        });
    }
}