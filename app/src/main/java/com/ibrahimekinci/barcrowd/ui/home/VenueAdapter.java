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

public class VenueAdapter extends ListAdapter<Venue, VenueAdapter.ViewHolder> {

    private final OnVenueClickListener listener;

    public interface OnVenueClickListener {
        void onVenueClick(Venue venue);
    }

    public VenueAdapter(OnVenueClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Venue> DIFF_CALLBACK = new DiffUtil.ItemCallback<Venue>() {
        @Override
        public boolean areItemsTheSame(@NonNull Venue oldItem, @NonNull Venue newItem) {
            return oldItem.getVenueId().equals(newItem.getVenueId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Venue oldItem, @NonNull Venue newItem) {
            // Check if visible content has changed
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getLastLiveUpdateCrowdLevel().equals(newItem.getLastLiveUpdateCrowdLevel());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using item_venue_search.xml as the standard card layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venue_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvInfo;
        private final ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_venue_name);
            tvInfo = itemView.findViewById(R.id.tv_venue_type); // ID matching item_venue_search.xml
            ivImage = itemView.findViewById(R.id.iv_venue_image);
        }

        public void bind(Venue venue, OnVenueClickListener listener) {
            tvName.setText(venue.getName());

            // Using new fields for display
            String crowd = venue.getLastLiveUpdateCrowdLevel() != null ? venue.getLastLiveUpdateCrowdLevel() : "-";
            String wait = venue.getLastLiveUpdateWaitTime() != null ? venue.getLastLiveUpdateWaitTime() : "-";

            String displayInfo = String.format("%s | Crowd: %s | Wait: %s min",
                    venue.getType(), crowd, wait);

            tvInfo.setText(displayInfo);

            if (venue.getLogoUrl() != null && !venue.getLogoUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(venue.getLogoUrl())
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            itemView.setOnClickListener(v -> listener.onVenueClick(venue));
        }
    }
}