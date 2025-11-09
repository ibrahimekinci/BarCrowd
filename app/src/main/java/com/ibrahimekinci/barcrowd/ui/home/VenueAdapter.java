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
import com.ibrahimekinci.barcrowd.domain.model.Venue;

public class VenueAdapter extends ListAdapter<Venue, VenueAdapter.VenueViewHolder> {

    public interface OnVenueClickListener {
        void onVenueClick(Venue venue);
    }

    private final OnVenueClickListener clickListener;
    public VenueAdapter(OnVenueClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }
    private static final DiffUtil.ItemCallback<Venue> DIFF_CALLBACK = new DiffUtil.ItemCallback<Venue>() {
        @Override
        public boolean areItemsTheSame(@NonNull Venue oldItem, @NonNull Venue newItem) {
            return oldItem.getVenueId().equals(newItem.getVenueId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Venue oldItem, @NonNull Venue newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getLastCrowdLevel().equals(newItem.getLastCrowdLevel());
        }
    };

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venue_card, parent, false);
        return new VenueViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        Venue venue = getItem(position);
        holder.bind(venue);
    }

    static class VenueViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivVenueLogo;
        private final TextView tvVenueName;
        private final TextView tvLastCrowdLevel;
        private final TextView tvLastWaitTime;
        private final TextView tvLastAgeGroup;
        private final OnVenueClickListener clickListener; // Added

        public VenueViewHolder(@NonNull View itemView, OnVenueClickListener clickListener) { // Updated
            super(itemView);
            this.clickListener = clickListener; // Added
            ivVenueLogo = itemView.findViewById(R.id.iv_venue_logo);
            tvVenueName = itemView.findViewById(R.id.tv_venue_name);
            tvLastCrowdLevel = itemView.findViewById(R.id.tv_last_crowd_level);
            tvLastWaitTime = itemView.findViewById(R.id.tv_last_wait_time);
            tvLastAgeGroup = itemView.findViewById(R.id.tv_last_age_group);
        }

        public void bind(Venue venue) {
            tvVenueName.setText(venue.getName());
            tvLastCrowdLevel.setText(venue.getLastCrowdLevel());
            tvLastWaitTime.setText(venue.getLastWaitTime());
            tvLastAgeGroup.setText(venue.getMostPopulousAge());

            Glide.with(itemView.getContext())
                    .load(venue.getLogoUrl())
                    .placeholder(R.drawable.venue_placeholder)
                    .error(R.drawable.venue_placeholder)
                    .centerCrop()
                    .into(ivVenueLogo);

            // Set the click listener for the entire item
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onVenueClick(venue);
                }
            });
        }
    }
}