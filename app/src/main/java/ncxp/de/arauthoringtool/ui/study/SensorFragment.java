package ncxp.de.arauthoringtool.ui.study;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.StudyActivity;
import ncxp.de.arauthoringtool.ui.study.adapter.SensorAdapter;
import ncxp.de.arauthoringtool.viewmodel.StudyViewModel;

public class SensorFragment extends Fragment {

	private StudyViewModel viewModel;
	private RecyclerView   sectionedRecyclerView;
	private SensorAdapter  sectionSensorAdapter;

	public static SensorFragment newInstance() {
		return new SensorFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = StudyActivity.obtainViewModel(getActivity());
		viewModel.init();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sensor, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		sectionedRecyclerView = view.findViewById(R.id.recyclerview_sectioned);
		setupSensorView();
	}

	private void setupSensorView() {
		sectionSensorAdapter = new SensorAdapter(new ArrayList<>());
		sectionedRecyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		sectionedRecyclerView.setLayoutManager(layoutManager);
		sectionedRecyclerView.setLayoutManager(layoutManager);
		sectionedRecyclerView.setAdapter(sectionSensorAdapter);
		viewModel.getSensors().observe(SensorFragment.this, (deviceSensors) -> {
			if (deviceSensors != null) {
				sectionSensorAdapter.replaceItems(viewModel.getSectionedDeviceSensors(deviceSensors));
			}
		});

	}
}
