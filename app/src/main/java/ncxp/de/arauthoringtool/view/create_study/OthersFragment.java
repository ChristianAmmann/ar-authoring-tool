package ncxp.de.arauthoringtool.view.create_study;

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

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.sensorlogger.SettingGroup;
import ncxp.de.arauthoringtool.view.create_study.adapter.OptionItem;
import ncxp.de.arauthoringtool.view.create_study.adapter.OptionType;
import ncxp.de.arauthoringtool.view.create_study.adapter.OtherAdapter;
import ncxp.de.arauthoringtool.view.create_study.adapter.SensorSettings;
import ncxp.de.arauthoringtool.viewmodel.StudyViewModel;

public class OthersFragment extends Fragment implements OptionOthersListener {

	private StudyViewModel viewModel;
	private RecyclerView   othersRecyclerView;

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
		OtherAdapter sectionAdapter = new OtherAdapter(new ArrayList<>(), getContext(), this);
		othersRecyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		othersRecyclerView.setLayoutManager(layoutManager);
		othersRecyclerView.setAdapter(sectionAdapter);
		sectionAdapter.addItems(getSectionedOptions());
	}


	private List<Object> getSectionedOptions() {
		List<Object> sectionedOptions = new ArrayList<>();
		sectionedOptions.add(SettingGroup.SENSOR_CONFIGURATION.getGroupId());
		if (viewModel.getSensorSettings() != null) {
			sectionedOptions.add(viewModel.getSensorSettings());
		} else {
			sectionedOptions.add(new SensorSettings());
		}
		sectionedOptions.add(SettingGroup.OTHERS.getGroupId());
		OptionItem taskCompletionTime = new OptionItem(getString(R.string.task_completion_time),
													   getString(R.string.task_completion_time_description),
													   OptionType.TASK_COMPLETION_TIME);
		taskCompletionTime.setActive(viewModel.isTaskCompletionTimeActive());
		OptionItem amountOfTouchEvents = new OptionItem(getString(R.string.touch_events), getString(R.string.touch_events_description), OptionType.AMOUNT_OF_TOUCH_EVENTS);
		amountOfTouchEvents.setActive(viewModel.isAmountOfTouchEventsActive());
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
		secondsPicker.setMaxValue(3600);
		secondsPicker.setMinValue(0);
		millisecondsPicker.setMinValue(0);
		millisecondsPicker.setMaxValue(999);
		secondsPicker.setValue(settings.getSeconds());
		int milliseconds = settings.getMilliseconds();
		millisecondsPicker.setValue(milliseconds);
		builder.setView(dialogView).setPositiveButton(R.string.save, (dialog, which) -> {
			int newSeconds = secondsPicker.getValue();
			int newMilliseconds = millisecondsPicker.getValue();
			double timeInterval = newSeconds + ((double) newMilliseconds / 1000);
			settings.setSensorMeasuringDistance(timeInterval);
			timeButton.setText(getString(R.string.time_format, timeInterval));
			viewModel.setSensorSettings(settings);

		}).setNegativeButton(R.string.cancel, null).create().show();
	}

	@Override
	public void onOptionItemClicked(OptionItem item) {
		switch (item.getType()) {
			case TASK_COMPLETION_TIME:
				viewModel.setTaskCompletionTime(item.isActive());
				break;
			case AMOUNT_OF_TOUCH_EVENTS:
				viewModel.setAmountOfTouchEvents(item.isActive());
				break;
		}
	}
}