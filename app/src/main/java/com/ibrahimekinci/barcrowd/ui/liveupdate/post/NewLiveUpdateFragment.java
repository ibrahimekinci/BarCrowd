package com.ibrahimekinci.barcrowd.ui.liveupdate.post;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.Venue;
import com.ibrahimekinci.barcrowd.util.AppLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewLiveUpdateFragment extends Fragment {

    private NewLiveUpdateViewModel viewModel;
    private NavController navController;

    // UI Components
    private AutoCompleteTextView actvVenue;
    private FrameLayout flPreviewContainer, loadingOverlay;
    private ImageView ivThumbnail, ivPlayIcon;
    private PlayerView pvVideo;
    private Button btnRecord, btnGallery, btnClear, btnPost;
    private TextView tvLoadingMessage;
    private RadioGroup rgCrowdLevel;
    private Spinner spinnerWaitTime, spinnerAgeRange;
    private TextInputEditText etDescription;

    private ExoPlayer exoPlayer;

    // Data
    private List<Venue> venueList = new ArrayList<>();
    private String selectedVenueId = null;
    private Uri selectedVideoUri = null;
    private Uri generatedThumbnailUri = null;

    // Launchers...
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> { if (uri != null) handleVideoSelection(uri); });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    if (videoUri != null) handleVideoSelection(videoUri);
                }
            });

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                boolean storageGranted = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    storageGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.READ_MEDIA_VIDEO, false));
                } else {
                    storageGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false));
                }
                if (Boolean.TRUE.equals(cameraGranted) && storageGranted) launchCamera();
                else showSnackbar(getString(R.string.msg_permission_required), true);
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String vid = NewLiveUpdateFragmentArgs.fromBundle(getArguments()).getVenueId();
            if (vid != null) selectedVenueId = vid;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_live_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        NewLiveUpdateViewModelFactory factory = new NewLiveUpdateViewModelFactory(
                injector.getGetAllVenuesUseCase(),
                injector.getPostLiveUpdateUseCase(),
                injector.getGetCurrentUserUseCase(),
                injector.getStorageWrapper()
        );
        viewModel = new ViewModelProvider(this, factory).get(NewLiveUpdateViewModel.class);

        findViews(view);
        setupUI();
        observeViewModel();
    }

    private void findViews(View view) {
        actvVenue = view.findViewById(R.id.actv_venue);
        flPreviewContainer = view.findViewById(R.id.fl_preview_container);
        ivThumbnail = view.findViewById(R.id.iv_thumbnail);
        ivPlayIcon = view.findViewById(R.id.iv_play_icon);
        pvVideo = view.findViewById(R.id.pv_video);
        btnClear = view.findViewById(R.id.btn_clear_video);
        btnRecord = view.findViewById(R.id.btn_record_video);
        btnGallery = view.findViewById(R.id.btn_select_gallery);
        rgCrowdLevel = view.findViewById(R.id.rg_crowd_level);
        spinnerWaitTime = view.findViewById(R.id.spinner_wait_time);
        spinnerAgeRange = view.findViewById(R.id.spinner_age_range);
        etDescription = view.findViewById(R.id.et_description);
        btnPost = view.findViewById(R.id.btn_post);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        tvLoadingMessage = view.findViewById(R.id.tv_loading_message);
    }

    private void setupUI() {
        // Adapters...
        ArrayAdapter<String> waitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"0–5", "5–15", "15–30", "30–45", "45+"});
        waitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWaitTime.setAdapter(waitAdapter);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"18–21", "21–24", "25–30", "30–35", "35+"});
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgeRange.setAdapter(ageAdapter);

        btnGallery.setOnClickListener(v -> galleryLauncher.launch("video/*"));
        btnRecord.setOnClickListener(v -> checkPermissionsAndLaunchCamera());
        btnClear.setOnClickListener(v -> clearSelection());
        ivPlayIcon.setOnClickListener(v -> startVideoPlayback());

        btnPost.setOnClickListener(v -> {
            if (selectedVenueId == null) {
                String currentText = actvVenue.getText().toString();
                for (Venue venue : venueList) {
                    if (venue.getName().equalsIgnoreCase(currentText)) {
                        selectedVenueId = venue.getVenueId();
                        break;
                    }
                }
            }

            String crowdLevel = getSelectedCrowdLevel();
            String waitTime = spinnerWaitTime.getSelectedItem().toString();
            String ageRange = spinnerAgeRange.getSelectedItem().toString();
            String description = etDescription.getText().toString();

            viewModel.submitUpdate(selectedVenueId, selectedVideoUri, generatedThumbnailUri, crowdLevel, waitTime, ageRange, description);
        });

        actvVenue.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for(Venue v : venueList) {
                if(v.getName().equals(selectedName)) {
                    selectedVenueId = v.getVenueId();
                    break;
                }
            }
        });
    }

    private void checkPermissionsAndLaunchCamera() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        boolean allGranted = true;
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false; break;
            }
        }
        if (allGranted) launchCamera(); else permissionLauncher.launch(permissions.toArray(new String[0]));
    }

    private void observeViewModel() {
        viewModel.getAllVenues().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                venueList = venues;
                setupVenueAdapter(venues);
            }
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnPost.setEnabled(!isLoading);
        });
        viewModel.getLoadingMessage().observe(getViewLifecycleOwner(), msg -> tvLoadingMessage.setText(msg));
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) showSnackbar(errorMsg, true);
        });
        viewModel.getPostSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                showSnackbar(getString(R.string.msg_post_success), false);
                navController.popBackStack();
            }
        });
    }

    private void setupVenueAdapter(List<Venue> venues) {
        String[] venueNames = new String[venues.size()];
        int preSelectedIndex = -1;
        for (int i = 0; i < venues.size(); i++) {
            venueNames[i] = venues.get(i).getName();
            if (selectedVenueId != null && venues.get(i).getVenueId().equals(selectedVenueId)) {
                preSelectedIndex = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, venueNames);
        actvVenue.setAdapter(adapter);
        if (preSelectedIndex != -1) actvVenue.setText(venueNames[preSelectedIndex], false);
    }

    private String getSelectedCrowdLevel() {
        int id = rgCrowdLevel.getCheckedRadioButtonId();
        if (id != -1) {
            RadioButton selectedBtn = getView().findViewById(id);
            return selectedBtn.getText().toString();
        }
        return "Medium";
    }

    private void launchCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        cameraLauncher.launch(takeVideoIntent);
    }

    private void handleVideoSelection(Uri videoUri) {
        selectedVideoUri = videoUri;
        btnRecord.setVisibility(View.GONE);
        btnGallery.setVisibility(View.GONE);
        flPreviewContainer.setVisibility(View.VISIBLE);
        ivThumbnail.setVisibility(View.VISIBLE);
        ivPlayIcon.setVisibility(View.VISIBLE);
        pvVideo.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(requireContext(), videoUri);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long durationMillis = Long.parseLong(time != null ? time : "0");
                if (durationMillis > 16000) {
                    if(getActivity()!=null) getActivity().runOnUiThread(()->{ showSnackbar(getString(R.string.msg_video_too_long), true); clearSelection(); });
                    return;
                }
                long middlePoint = (durationMillis / 2) * 1000;
                Bitmap bitmap = retriever.getFrameAtTime(middlePoint, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                if (bitmap == null) bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                retriever.release();

                if (bitmap != null) {
                    File cacheDir = requireContext().getCacheDir();
                    File thumbFile = File.createTempFile("thumb_", ".jpg", cacheDir);
                    FileOutputStream fos = new FileOutputStream(thumbFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                    fos.close();
                    generatedThumbnailUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", thumbFile);
                    if (getActivity() != null) {
                        final Bitmap finalBitmap = bitmap;
                        getActivity().runOnUiThread(() -> Glide.with(this).load(finalBitmap).into(ivThumbnail));
                    }
                }
            } catch (Exception e) { AppLogger.e("Video processing error", e); }
        }).start();
    }

    private void startVideoPlayback() {
        if (selectedVideoUri == null) return;
        ivThumbnail.setVisibility(View.GONE);
        ivPlayIcon.setVisibility(View.GONE);
        pvVideo.setVisibility(View.VISIBLE);
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        pvVideo.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(selectedVideoUri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    private void clearSelection() {
        selectedVideoUri = null;
        generatedThumbnailUri = null;
        if (exoPlayer != null) { exoPlayer.release(); exoPlayer = null; }
        flPreviewContainer.setVisibility(View.GONE);
        btnRecord.setVisibility(View.VISIBLE);
        btnGallery.setVisibility(View.VISIBLE);
    }

    @Override public void onStop() { super.onStop(); if (exoPlayer != null) exoPlayer.pause(); }
    @Override public void onDestroyView() { super.onDestroyView(); if (exoPlayer != null) { exoPlayer.release(); exoPlayer = null; } }
    private void showSnackbar(String msg, boolean isError) {
        if (getView() == null) return;
        Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);
        if (isError) snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark));
        snackbar.show();
    }
}