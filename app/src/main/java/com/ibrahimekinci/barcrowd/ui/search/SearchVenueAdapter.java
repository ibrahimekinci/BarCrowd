package com.ibrahimekinci.barcrowd.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.domain.model.Venue;

import java.util.ArrayList;
import java.util.List;

public class SearchVenueAdapter extends RecyclerView.Adapter<SearchVenueAdapter.ViewHolder> {

    private List<Venue> venues = new ArrayList<>();
    private final OnVenueClickListener listener;

    public interface OnVenueClickListener {
        void onVenueClick(Venue venue);
    }

    public SearchVenueAdapter(OnVenueClickListener listener) {
        this.listener = listener;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venue_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(venues.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return venues != null ? venues.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvInfo;
        private final ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_venue_name);
            // ID 'tv_venue_type' is used for the info line
            tvInfo = itemView.findViewById(R.id.tv_venue_type);
            ivImage = itemView.findViewById(R.id.iv_venue_image);
        }

        public void bind(Venue venue, OnVenueClickListener listener) {
            tvName.setText(venue.getName());

            // Format: Type | Crowd | Wait | Age
            String crowd = venue.getLastLiveUpdateCrowdLevel() != null ? venue.getLastLiveUpdateCrowdLevel() : "-";
            String wait = venue.getLastLiveUpdateWaitTime() != null ? venue.getLastLiveUpdateWaitTime() : "-";
            String age = venue.getLastLiveUpdateAgeRange() != null ? venue.getLastLiveUpdateAgeRange() : "-";
            String type = venue.getType() != null ? venue.getType() : "Venue";

            String infoText = String.format("%s • %s • %s min • %s", type, crowd, wait, age);

            tvInfo.setText(infoText);

            if (venue.getImageUrl() != null && !venue.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(venue.getImageUrl())
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