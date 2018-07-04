package ncxp.de.mobiledatacollection.ui.study;

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

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.StudyActivity;
import ncxp.de.mobiledatacollection.datalogger.SettingGroup;
import ncxp.de.mobiledatacollection.model.data.CapturingData;
import ncxp.de.mobiledatacollection.model.data.SensorSettings;
import ncxp.de.mobiledatacollection.ui.study.adapter.OtherAdapter;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.StudyViewModel;

public class OthersFragment extends Fragment {

	private StudyViewModel viewModel;
	private RecyclerView   othersRecyclerView;
	private OtherAdapter   sectionAdapter;

	public static OthersFragment newInstance() {
		return new OthersFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = StudyActivity.obtainViewModel(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_others, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		othersRecyclerView = view.findViewById(R.id.recyclerview_other);
		setupOtherView();
	}

	private void setupOtherView() {
		sectionAdapter = new OtherAdapter(new ArrayList<>());
		othersRecyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		othersRecyclerView.setLayoutManager(layoutManager);
		othersRecyclerView.setAdapter(sectionAdapter);
		sectionAdapter.addItems(getSectionedOptions());
	}


	private List<Object> getSectionedOptions() {
		List<Object> sectionedOptions = new ArrayList<>();

		sectionedOptions.add(SettingGroup.SENSOR_CONFIGURATION.getGroupId());
		sectionedOptions.add(new SensorSettings());
		sectionedOptions.add(SettingGroup.VIDEO_AUDIO.getGroupId());
		CapturingData videoData = new CapturingData("Bildschrim aufzeichnen", "Während der Studie wird der Bildschrim vom Probanden aufgezeichnet");
		CapturingData audioData = new CapturingData("Ton aufzeichnen", "Während der Studie wird der Ton vom Probanden aufgezeichnet");
		sectionedOptions.add(videoData);
		sectionedOptions.add(audioData);
		sectionedOptions.add(SettingGroup.OTHERS.getGroupId());
		CapturingData taskCompletionTime = new CapturingData("Bearbeitungszeit", "Die benötigte Zeit eines Probanden um die Studie zu absolvieren");
		CapturingData amountOfTouchEvents = new CapturingData("Anzahl der Touch-Eingaben", "Benötigte Anzahl der Touch-Eingaben um die Studie zu absolvieren");
		sectionedOptions.add(taskCompletionTime);
		sectionedOptions.add(amountOfTouchEvents);
		return sectionedOptions;
	}
}