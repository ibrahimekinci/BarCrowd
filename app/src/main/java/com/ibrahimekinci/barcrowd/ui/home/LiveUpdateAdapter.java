package com.ibrahimekinci.barcrowd.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

public class LiveUpdateAdapter extends ListAdapter<LiveUpdate, LiveUpdateAdapter.LiveUpdateViewHolder> {

    // Interface for click events
    public interface OnLiveUpdateClickListener {
        void onLiveUpdateClick(LiveUpdate liveUpdate);
    }

    // Member variable to hold the listener
    private final OnLiveUpdateClickListener clickListener;

    // Constructor accepting the listener
    public LiveUpdateAdapter(OnLiveUpdateClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<LiveUpdate> DIFF_CALLBACK = new DiffUtil.ItemCallback<LiveUpdate>() {
        @Override
        public boolean areItemsTheSame(@NonNull LiveUpdate oldItem, @NonNull LiveUpdate newItem) {
            return oldItem.getUpdateId().equals(newItem.getUpdateId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull LiveUpdate oldItem, @NonNull LiveUpdate newItem) {
            return oldItem.getCrowdLevel().equals(newItem.getCrowdLevel());
        }
    };

    @NonNull
    @Override
    public LiveUpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_live_update_card, parent, false);
        return new LiveUpdateViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveUpdateViewHolder holder, int position) {
        LiveUpdate update = getItem(position);
        holder.bind(update);
    }

    static class LiveUpdateViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumbnail;
        private final TextView tvVenueName;
        private final TextView tvCrowdLevel;
        private final TextView tvWaitTime;
        private final TextView tvAgeGroup;
        private final OnLiveUpdateClickListener clickListener;

        public LiveUpdateViewHolder(@NonNull View itemView, OnLiveUpdateClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvVenueName = itemView.findViewById(R.id.tv_venue_name);
            tvCrowdLevel = itemView.findViewById(R.id.tv_crowd_level);
            tvWaitTime = itemView.findViewById(R.id.tv_wait_time);
            tvAgeGroup = itemView.findViewById(R.id.tv_age_group);
        }

        public void bind(LiveUpdate update) {
            // Bind data to views
            tvVenueName.setText(update.getVenueName());
            tvCrowdLevel.setText(update.getCrowdLevel());
            tvWaitTime.setText(update.getWaitTime());
            tvAgeGroup.setText(update.getAgeRange());

            String imageUrl = update.getThumbnailUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                // Fallback to venue logo if no thumbnail exists
                imageUrl = update.getVenueLogoUrl();
            }

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.liveupdate_placeholder)
                    .error(R.drawable.liveupdate_placeholder)
                    .centerCrop()
                    .into(ivThumbnail);

            // Set click listener on the item view
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onLiveUpdateClick(update);
                }
            });
        }
    }
}