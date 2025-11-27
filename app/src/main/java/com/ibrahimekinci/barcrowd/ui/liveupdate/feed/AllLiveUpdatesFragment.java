package com.ibrahimekinci.barcrowd.ui.liveupdate.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.di.DependencyInjector;
import com.ibrahimekinci.barcrowd.domain.model.LiveUpdate;

public class AllLiveUpdatesFragment extends Fragment implements LiveFeedAdapter.OnItemClickListener {

    private AllLiveUpdatesViewModel viewModel;
    private LiveFeedAdapter adapter;
    private NavController navController;

    private RecyclerView rvFeed;
    private ProgressBar pbLoading;
    private TextView tvEmptyView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_live_updates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // View Tanımlamaları
        rvFeed = view.findViewById(R.id.rv_live_feed);
        pbLoading = view.findViewById(R.id.pb_loading);
        tvEmptyView = view.findViewById(R.id.tv_empty_view);

        setupRecyclerView();
        setupViewModel();
        observeViewModel();
    }

    private void setupViewModel() {
        DependencyInjector injector = ((BarCrowdApplication) requireActivity().getApplication()).getDependencyInjector();
        AllLiveUpdatesViewModelFactory factory = new AllLiveUpdatesViewModelFactory(
                injector.getGetAllLiveUpdatesUseCase()
        );
        viewModel = new ViewModelProvider(this, factory).get(AllLiveUpdatesViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new LiveFeedAdapter(this); // 'this' ile tıklama listener'ını gönderiyoruz
        rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFeed.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getLiveFeedUpdates().observe(getViewLifecycleOwner(), updates -> {
            if (updates == null || updates.isEmpty()) {
                tvEmptyView.setVisibility(View.VISIBLE);
                rvFeed.setVisibility(View.GONE);
            } else {
                tvEmptyView.setVisibility(View.GONE);
                rvFeed.setVisibility(View.VISIBLE);
                adapter.setUpdates(updates);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    // Adaptör Tıklama Hareketi
    @Override
    public void onItemClick(LiveUpdate update) {
        try {
            // Safe Args kütüphanesi ile LiveUpdate objesini gönderiyoruz.
            // Bu action'ın nav_graph.xml'de tanımlı olması gerekir.
            AllLiveUpdatesFragmentDirections.ActionAllLiveUpdatesToDetail action =
                    AllLiveUpdatesFragmentDirections.actionAllLiveUpdatesToDetail(update);
            navController.navigate(action);
        } catch (Exception e) {
            // Navigasyon hatası (genellikle nav_graph.xml'de action ID'si yanlışsa oluşur)
            Toast.makeText(getContext(), "Detay sayfasına yönlendirme hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}