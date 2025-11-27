package com.ibrahimekinci.barcrowd.ui.liveupdate.details;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LiveUpdateDetailFragment extends Fragment {

    private LiveUpdateDetailViewModel viewModel;
    private NavController navController;
    private LiveUpdate liveUpdate;

    // Views
    private ImageView ivMedia;
    private PlayerView pvMedia;
    private TextView tvVenueName, tvCreatedAt, tvCrowdLevel, tvWaitTime, tvAgeRange, tvDescription;
    private Button btnDelete;
    private FrameLayout loadingOverlay;
    private MaterialToolbar toolbar;

    private ExoPlayer exoPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            liveUpdate = LiveUpdateDetailFragmentArgs.fromBundle(getArguments()).getLiveUpdate();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_update_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        findViews(view);

        if (liveUpdate == null) {
            AppLogger.e("LiveUpdateDetailFragment: LiveUpdate arg is null");
            navController.popBackStack();
            return;
        }

        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        LiveUpdateDetailViewModelFactory factory = new LiveUpdateDetailViewModelFactory(
                injector.getGetCurrentUserUseCase(),
                injector.getSoftDeleteLiveUpdateUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(LiveUpdateDetailViewModel.class);

        setupUI();
        setupListeners();
        observeViewModel();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        ivMedia = view.findViewById(R.id.iv_media);
        pvMedia = view.findViewById(R.id.pv_media);
        tvVenueName = view.findViewById(R.id.tv_venue_name);
        tvCreatedAt = view.findViewById(R.id.tv_created_at);
        tvCrowdLevel = view.findViewById(R.id.tv_crowd_level);
        tvWaitTime = view.findViewById(R.id.tv_wait_time);
        tvAgeRange = view.findViewById(R.id.tv_age_range);
        tvDescription = view.findViewById(R.id.tv_description);
        btnDelete = view.findViewById(R.id.btn_delete);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
    }

    private void setupUI() {
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        String vName = liveUpdate.getVenueName();
        if (vName == null || vName.isEmpty()) vName = liveUpdate.getVenueId();
        tvVenueName.setText(vName);

        tvCrowdLevel.setText(liveUpdate.getCrowdLevel());
        tvWaitTime.setText(liveUpdate.getWaitTime());
        tvAgeRange.setText(liveUpdate.getAgeRange());
        tvDescription.setText(liveUpdate.getDescription());

        if (liveUpdate.getCreatedAt() != null) {
            Date date = liveUpdate.getCreatedAt().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            tvCreatedAt.setText("Posted on " + sdf.format(date));
        }

        setupMedia(liveUpdate.getMediaUrl());
    }

    private void setupMedia(String url) {
        if (url == null || url.isEmpty()) return;

        // Basit video kontrolü (.mp4 içeriyor mu veya path içinde "videos" var mı)
        boolean isVideo = url.contains(".mp4") || url.contains("video");

        if (isVideo) {
            ivMedia.setVisibility(View.GONE);
            pvMedia.setVisibility(View.VISIBLE);
            initializePlayer(url);
        } else {
            pvMedia.setVisibility(View.GONE);
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.liveupdate_placeholder)
                    .error(R.drawable.liveupdate_placeholder)
                    .into(ivMedia);
        }
    }

    private void initializePlayer(String url) {
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        pvMedia.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    private void setupListeners() {
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && liveUpdate != null) {
                // Eğer giriş yapan kullanıcı bu postun sahibiyse Silme butonunu göster
                if (user.getUserId().equals(liveUpdate.getUserId())) {
                    btnDelete.setVisibility(View.VISIBLE);
                } else {
                    btnDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> performDelete())
                .show();
    }

    private void performDelete() {
        loadingOverlay.setVisibility(View.VISIBLE);

        // DÜZELTME: LiveUpdateRepository.DeleteCallback kullanılıyor
        viewModel.deleteUpdate(liveUpdate, new LiveUpdateRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                if (getContext() == null) return;
                loadingOverlay.setVisibility(View.GONE);
                Snackbar.make(requireView(), R.string.msg_delete_success, Snackbar.LENGTH_SHORT).show();
                navController.popBackStack();
            }

            @Override
            public void onError(Exception e) {
                if (getContext() == null) return;
                loadingOverlay.setVisibility(View.GONE);
                AppLogger.e("Delete failed", e);
                Snackbar.make(requireView(), getString(R.string.msg_delete_error, e.getMessage()), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}