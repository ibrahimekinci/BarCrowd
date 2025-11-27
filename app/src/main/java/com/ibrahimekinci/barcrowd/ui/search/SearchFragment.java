package com.ibrahimekinci.barcrowd.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ibrahimekinci.barcrowd.R;
import com.ibrahimekinci.barcrowd.domain.model.VenueFilterOptions;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private Spinner spType, spCrowd, spWait, spAge;
    private Button btnShowResults;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        searchView = view.findViewById(R.id.sv_venue_name);
        spType = view.findViewById(R.id.spinner_venue_type);
        spCrowd = view.findViewById(R.id.spinner_crowd_level);
        spWait = view.findViewById(R.id.spinner_wait_time);
        spAge = view.findViewById(R.id.spinner_age_range);
        btnShowResults = view.findViewById(R.id.btn_show_results);

        setupSpinners();

        btnShowResults.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            String type = spType.getSelectedItem().toString();
            String crowd = spCrowd.getSelectedItem().toString();
            String wait = spWait.getSelectedItem().toString();
            String age = spAge.getSelectedItem().toString();

            VenueFilterOptions options = new VenueFilterOptions(
                    query, type, crowd, wait, age
            );

            SearchFragmentDirections.ActionSearchToResults action =
                    SearchFragmentDirections.actionSearchToResults(options);
            navController.navigate(action);
        });
    }

    private void setupSpinners() {
        setupSpinner(spType, new String[]{"Any", "Bar", "Club", "Pub", "Restaurant"});
        setupSpinner(spCrowd, new String[]{"Any", "Low", "Medium", "High"});
        setupSpinner(spWait, new String[]{"Any", "0–5", "5–15", "15–30", "30–45", "45+"});
        setupSpinner(spAge, new String[]{"Any", "18–21", "21–24", "25–30", "30–35", "35+"});
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}