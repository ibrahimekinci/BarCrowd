package com.ibrahimekinci.barcrowd.ui.liveupdate.myliveupdate;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

import java.util.ArrayList;
import java.util.List;

public class MyLiveUpdatesAdapter extends RecyclerView.Adapter<MyLiveUpdatesAdapter.ViewHolder> {

    private List<LiveUpdate> updates = new ArrayList<>();
    private final OnUpdateActionListener actionListener;

    // Interface to handle clicks in Fragment
    public interface OnUpdateActionListener {
        void onItemClick(LiveUpdate update); // Go to details
        void onDeleteClick(LiveUpdate update); // Delete item
    }

    public MyLiveUpdatesAdapter(OnUpdateActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setUpdates(List<LiveUpdate> updates) {
        this.updates = updates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_live_update, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LiveUpdate update = updates.get(position);
        holder.bind(update, actionListener);
    }

    @Override
    public int getItemCount() {
        return updates != null ? updates.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar, ivThumbnail;
        private final TextView tvVenueName, tvTimeAgo, tvDescription;
        private final Chip chipCrowd, chipWait, chipAge;
        private final ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvVenueName = itemView.findViewById(R.id.tv_venue_name);
            tvTimeAgo = itemView.findViewById(R.id.tv_time_ago);
            tvDescription = itemView.findViewById(R.id.tv_description);
            chipCrowd = itemView.findViewById(R.id.chip_crowd);
            chipWait = itemView.findViewById(R.id.chip_wait);
            chipAge = itemView.findViewById(R.id.chip_age);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(LiveUpdate update, OnUpdateActionListener listener) {
            // 1. Venue Name (Use VenueName if available, else fallback)
            String vName = update.getVenueName() != null ? update.getVenueName() : "Unknown Venue";
            tvVenueName.setText(vName);

            // 2. Time Ago
            if (update.getCreatedAt() != null) {
                long timeMillis = update.getCreatedAt().toDate().getTime();
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(timeMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                tvTimeAgo.setText(timeAgo);
            } else {
                tvTimeAgo.setText("Just now");
            }

            // 3. Stats
            chipCrowd.setText(update.getCrowdLevel());
            chipWait.setText(update.getWaitTime() + " min");
            chipAge.setText(update.getAgeRange());

            // 4. Description
            if (update.getDescription() != null && !update.getDescription().isEmpty()) {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(update.getDescription());
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // 6. THUMBNAIL
            if (update.getThumbnailUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(update.getThumbnailUrl())
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
                        .into(ivThumbnail);
            }

            // 7. Click Listeners
            itemView.setOnClickListener(v -> listener.onItemClick(update));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(update));
        }
    }
}