package ncxp.de.mobiledatacollection.ui.study;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.StudyActivity;
import ncxp.de.mobiledatacollection.datalogger.SettingGroup;
import ncxp.de.mobiledatacollection.ui.study.adapter.OptionItem;
import ncxp.de.mobiledatacollection.ui.study.adapter.OptionType;
import ncxp.de.mobiledatacollection.ui.study.adapter.OtherAdapter;
import ncxp.de.mobiledatacollection.ui.study.adapter.SensorSettings;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.OptionOthersListener;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.StudyViewModel;

public class OthersFragment extends Fragment implements OptionOthersListener {

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
		sectionAdapter = new OtherAdapter(new ArrayList<>(), this);
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
		OptionItem videoItem = new OptionItem(getString(R.string.screen_capturing), getString(R.string.screen_capturing_description), OptionType.VIDEO);
		OptionItem audioItem = new OptionItem(getString(R.string.audio_capturing), getString(R.string.audio_capturing_description), OptionType.AUDIO);
		sectionedOptions.add(videoItem);
		sectionedOptions.add(audioItem);
		sectionedOptions.add(SettingGroup.OTHERS.getGroupId());
		OptionItem taskCompletionTime = new OptionItem(getString(R.string.task_completion_time),
													   getString(R.string.task_completion_time_description),
													   OptionType.TASK_COMPLETION_TIME);
		OptionItem amountOfTouchEvents = new OptionItem(getString(R.string.touch_events), getString(R.string.touch_events_description), OptionType.AMOUNT_OF_TOUCH_EVENTS);
		sectionedOptions.add(taskCompletionTime);
		sectionedOptions.add(amountOfTouchEvents);
		return sectionedOptions;
	}

	@Override
	public void onTimePickerClicked(Button timeButton, SensorSettings settings) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		LayoutInflater inflater = getLayoutInflater();
		builder.setTitle(R.string.settings_sensor_timeinterval_title);
		View dialogView = inflater.inflate(R.layout.dialog_time_picker, null);
		NumberPicker secondsPicker = dialogView.findViewById(R.id.seconds);
		NumberPicker millisecondsPicker = dialogView.findViewById(R.id.milliseconds);
		int seconds = (int) settings.getSensorMeasuringDistance();
		int milliseconds = (int) ((settings.getSensorMeasuringDistance() - (int) settings.getSensorMeasuringDistance()) * 100);
		secondsPicker.setMaxValue(3600);
		secondsPicker.setMinValue(0);
		millisecondsPicker.setMinValue(0);
		millisecondsPicker.setMaxValue(999);
		secondsPicker.setValue(seconds);
		millisecondsPicker.setValue(milliseconds);
		builder.setView(dialogView).setPositiveButton(R.string.save, (dialog, which) -> {
			int newSeconds = secondsPicker.getValue();
			int newMilliseconds = millisecondsPicker.getValue();
			double timeInterval = newSeconds + newMilliseconds / 1000;
			settings.setSensorMeasuringDistance(timeInterval);
			timeButton.setText(getString(R.string.time_format, newSeconds, newMilliseconds));
		}).setNegativeButton(R.string.cancel, null).create().show();
	}
}