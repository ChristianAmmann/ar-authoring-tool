package ncxp.de.mobiledatacollection.ui.study;


import android.arch.lifecycle.ViewModelProviders;
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

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.ui.study.adapter.SensorAdapter;

public class SensorFragment extends Fragment {

	private StudyViewModel viewModel;
	private RecyclerView   sensorRecyclerView;
	private SensorAdapter  sensorAdapter;

	public static SensorFragment newInstance() {
		return new SensorFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.configureViewModel();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sensor_fragment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		sensorRecyclerView = view.findViewById(R.id.recyclerview_sensor);
		setupSensorView();
	}

	private void setupSensorView() {
		sensorRecyclerView.setHasFixedSize(true);
		sensorAdapter = new SensorAdapter(new ArrayList<>());
		sensorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		sensorRecyclerView.setAdapter(sensorAdapter);
		sensorAdapter.addItems(viewModel.getAvailableDeviceSensor());
	}

	private void configureViewModel() {
		StudyDao study = StudyDatabase.getInstance(getContext()).study();
		SurveyDao survey = StudyDatabase.getInstance(getContext()).survey();
		StudyRepository studyRepo = new StudyRepository(study);
		SurveyRepository surveyRepo = new SurveyRepository(survey);
		SensorDataManager sensorDataManager = SensorDataManager.getInstance(getContext());
		StudyViewModelFactory factory = new StudyViewModelFactory(studyRepo, surveyRepo, sensorDataManager);
		viewModel = ViewModelProviders.of(this, factory).get(StudyViewModel.class);
		viewModel.init();
	}
}
