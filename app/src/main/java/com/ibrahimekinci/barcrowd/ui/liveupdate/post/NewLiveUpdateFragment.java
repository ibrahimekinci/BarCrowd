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
import com.google.android.material.chip.ChipGroup;
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

/**
 * Fragment to handle creating and posting a new live update.
 * Supports 15s video recording and thumbnail generation.
 */
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
    private ChipGroup chipGroupCrowd;
    private Spinner spinnerWaitTime, spinnerAgeRange;
    private TextInputEditText etDescription;

    private ExoPlayer exoPlayer;

    // Data
    private List<Venue> venueList = new ArrayList<>();
    private String selectedVenueId = null;
    private Uri selectedVideoUri = null;
    private Uri generatedThumbnailUri = null; // The thumbnail file we create

    // --- Launchers ---

    // 1. Gallery Picker (Video Only)
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    handleVideoSelection(uri);
                }
            }
    );

    // 2. Camera Capture (Video)
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    if (videoUri != null) {
                        handleVideoSelection(videoUri);
                    }
                }
            }
    );

    // 3. Permission Request
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                // Check CAMERA permission
                Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);

                // Check Storage/Media permissions depending on API level
                boolean storageGranted = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    storageGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.READ_MEDIA_VIDEO, false));
                } else {
                    storageGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false));
                }

                if (cameraGranted != null && cameraGranted && storageGranted) {
                    launchCamera();
                } else {
                    showSnackbar(getString(R.string.msg_permission_required), true);
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String vid = NewLiveUpdateFragmentArgs.fromBundle(getArguments()).getVenueId();
            if (vid != null) selectedVenueId = vid;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        chipGroupCrowd = view.findViewById(R.id.chip_group_crowd);
        spinnerWaitTime = view.findViewById(R.id.spinner_wait_time);
        spinnerAgeRange = view.findViewById(R.id.spinner_age_range);
        etDescription = view.findViewById(R.id.et_description);
        btnPost = view.findViewById(R.id.btn_post);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        tvLoadingMessage = view.findViewById(R.id.tv_loading_message);
    }

    private void setupUI() {
        // Spinners
        ArrayAdapter<String> waitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"0–5", "5–15", "15–30", "30–45", "45+"});
        waitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWaitTime.setAdapter(waitAdapter);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"18–21", "21–24", "25–30", "30–35", "35+"});
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgeRange.setAdapter(ageAdapter);

        // Listeners
        btnGallery.setOnClickListener(v -> galleryLauncher.launch("video/*"));

        btnRecord.setOnClickListener(v -> checkPermissionsAndLaunchCamera());

        btnClear.setOnClickListener(v -> clearSelection());

        // Clicking the play overlay starts the video
        ivPlayIcon.setOnClickListener(v -> startVideoPlayback());

        btnPost.setOnClickListener(v -> {
            String crowdLevel = getSelectedCrowdLevel();
            String waitTime = spinnerWaitTime.getSelectedItem().toString();
            String ageRange = spinnerAgeRange.getSelectedItem().toString();
            String description = etDescription.getText().toString();

            // Call ViewModel with BOTH Video and Thumbnail URIs
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
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            launchCamera();
        } else {
            permissionLauncher.launch(permissions.toArray(new String[0]));
        }
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

        viewModel.getLoadingMessage().observe(getViewLifecycleOwner(), msg -> {
            tvLoadingMessage.setText(msg);
        });

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

        if (preSelectedIndex != -1) {
            actvVenue.setText(venueNames[preSelectedIndex], false);
        }
    }

    private String getSelectedCrowdLevel() {
        int id = chipGroupCrowd.getCheckedChipId();
        if (id == R.id.chip_low) return "Low";
        if (id == R.id.chip_medium) return "Medium";
        if (id == R.id.chip_high) return "High";
        return null;
    }

    private void launchCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Limit duration to 15 seconds
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);

        // IMPORTANT: Set video quality to LOW (0) for emulators to prevent freezing/crashing.
        // On real devices, you can set this to 1 (High).
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

        cameraLauncher.launch(takeVideoIntent);
    }

    /**
     * Handles the selected video URI.
     * 1. Sets state (show preview, hide buttons).
     * 2. Generates a thumbnail Bitmap from the video.
     * 3. Saves the bitmap to a local temp file to get a URI for uploading.
     */
    private void handleVideoSelection(Uri videoUri) {
        selectedVideoUri = videoUri;

        // UI Updates
        btnRecord.setVisibility(View.GONE);
        btnGallery.setVisibility(View.GONE);
        flPreviewContainer.setVisibility(View.VISIBLE);

        // Show initial state: Image + Play Button
        ivThumbnail.setVisibility(View.VISIBLE);
        ivPlayIcon.setVisibility(View.VISIBLE);
        pvVideo.setVisibility(View.GONE);

        // Generate Thumbnail in background to avoid UI freeze
        new Thread(() -> {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(requireContext(), videoUri);

                // 1. Check Duration
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long durationMillis = Long.parseLong(time);

                // 15 seconds + slight buffer
                if (durationMillis > 16000) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showSnackbar(getString(R.string.msg_video_too_long), true);
                            clearSelection(); // Reset if too long
                        });
                    }
                    return;
                }

                // 2. Extract Middle Frame
                long middlePoint = (durationMillis / 2) * 1000; // Microseconds
                Bitmap bitmap = retriever.getFrameAtTime(middlePoint, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                // If middle frame fails, try the first frame
                if (bitmap == null) {
                    bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                }

                retriever.release();

                if (bitmap != null) {
                    // 3. Save Thumbnail to Cache File
                    File cacheDir = requireContext().getCacheDir();
                    File thumbFile = File.createTempFile("thumb_", ".jpg", cacheDir);
                    FileOutputStream fos = new FileOutputStream(thumbFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                    fos.close();

                    generatedThumbnailUri = FileProvider.getUriForFile(
                            requireContext(),
                            requireContext().getPackageName() + ".provider",
                            thumbFile
                    );

                    // Update UI on main thread
                    if (getActivity() != null) {
                        final Bitmap finalBitmap = bitmap;
                        getActivity().runOnUiThread(() -> {
                            Glide.with(this).load(finalBitmap).into(ivThumbnail);
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> showSnackbar(getString(R.string.msg_error_thumbnail), true));
                    }
                }

            } catch (Exception e) {
                AppLogger.e("Video processing error", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> showSnackbar("Error processing video", true));
                }
            }
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
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        flPreviewContainer.setVisibility(View.GONE);
        btnRecord.setVisibility(View.VISIBLE);
        btnGallery.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void showSnackbar(String msg, boolean isError) {
        if (getView() == null) return;
        Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);
        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark));
        }
        snackbar.show();
    }
}