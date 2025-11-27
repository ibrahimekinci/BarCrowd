package com.ibrahimekinci.barcrowd.ui.liveupdate.feed;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class LiveFeedAdapter extends RecyclerView.Adapter<LiveFeedAdapter.ViewHolder> {

    private List<LiveUpdate> updates = new ArrayList<>();
    private final OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(LiveUpdate update);
    }

    public LiveFeedAdapter(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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
        holder.bind(update, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return updates != null ? updates.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar, ivThumbnail;
        private final TextView tvVenueName, tvTimeAgo, tvDescription;
        private final Chip chipCrowd, chipWait, chipAge;
        private final View btnDelete;

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

            if (btnDelete != null) btnDelete.setVisibility(View.GONE);
        }

        public void bind(LiveUpdate update, OnItemClickListener listener) {
            String vName = update.getVenueName() != null ? update.getVenueName() : "Unknown Venue";
            tvVenueName.setText(vName);

            if (update.getCreatedAt() != null) {
                long timeMillis = update.getCreatedAt().toDate().getTime();
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(timeMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                tvTimeAgo.setText(timeAgo);
            }

            chipCrowd.setText(update.getCrowdLevel());
            chipWait.setText(update.getWaitTime() + " m");
            chipAge.setText(update.getAgeRange());
            tvDescription.setText(update.getDescription());

            // Thumbnail Loading
            if (update.getThumbnailUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(update.getThumbnailUrl())
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
                        .into(ivThumbnail);
            }

            if (update.getVenueLogoUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(update.getVenueLogoUrl())
                        .circleCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .into(ivAvatar);
            } else {
                // Fallback
                ivAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(update));
        }
    }
}