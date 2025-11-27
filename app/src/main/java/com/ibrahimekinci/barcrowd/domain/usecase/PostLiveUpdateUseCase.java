package com.ibrahimekinci.barcrowd.domain.usecase;

import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.data.repository.VenueRepository;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

public class PostLiveUpdateUseCase {

    private final LiveUpdateRepository liveUpdateRepository;
    private final VenueRepository venueRepository;

    public interface Callback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public PostLiveUpdateUseCase(LiveUpdateRepository liveUpdateRepository, VenueRepository venueRepository) {
        this.liveUpdateRepository = liveUpdateRepository;
        this.venueRepository = venueRepository;
    }

    public void execute(LiveUpdate liveUpdate, Callback callback) {
        liveUpdateRepository.saveLiveUpdate(liveUpdate, new LiveUpdateRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                if (liveUpdate.getVenueId() != null) {
                    venueRepository.updateVenueStats(
                            liveUpdate.getVenueId(),
                            liveUpdate.getCrowdLevel(),
                            liveUpdate.getWaitTime(),
                            liveUpdate.getAgeRange()
                    );
                }
                callback.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}