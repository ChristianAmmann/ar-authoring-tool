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
import java.util.List;
import java.util.stream.Collectors;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.datalogger.AvailableDeviceSensor;
import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.datalogger.SensorGroup;
import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.ui.study.adapter.SensorAdapter;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.StudyViewModel;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.StudyViewModelFactory;

public class SensorFragment extends Fragment implements OptionSensorListener {

	private StudyViewModel viewModel;
	private RecyclerView   sectionedRecyclerView;
	private SensorAdapter  sectionSensorAdapter;

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
		sectionSensorAdapter.addItems(getSectionedDeviceSensors(viewModel.getAvailableDeviceSensor().getValue()));
	}

	private void configureViewModel() {
		StudyDao study = StudyDatabase.getInstance(getContext()).study();
		SurveyDao survey = StudyDatabase.getInstance(getContext()).survey();
		StudyRepository studyRepo = new StudyRepository(study);
		SurveyRepository surveyRepo = new SurveyRepository(survey);
		SensorDataManager sensorDataManager = SensorDataManager.getInstance(getContext());
		StudyViewModelFactory factory = new StudyViewModelFactory(studyRepo, surveyRepo, sensorDataManager);
		viewModel = ViewModelProviders.of(getActivity(), factory).get(StudyViewModel.class);
		viewModel.init();
	}

	private List<Object> getSectionedDeviceSensors(List<AvailableDeviceSensor> availableDeviceSensors) {
		List<Object> sectionedDeviceSensors = new ArrayList<>();
		for (SensorGroup group : SensorGroup.values()) {
			List<AvailableDeviceSensor> sensorsOfGroup = availableDeviceSensors.stream()
																			   .filter(availableDeviceSensor -> availableDeviceSensor.getType().getGroup().equals(group))
																			   .collect(Collectors.toList());
			sectionedDeviceSensors.add(group.getGroupId());
			sectionedDeviceSensors.addAll(sensorsOfGroup);
		}
		return sectionedDeviceSensors;
	}

	@Override
	public void onActiveChanged(AvailableDeviceSensor deviceSensor, boolean active) {
		deviceSensor.setActive(active);
	}
}
