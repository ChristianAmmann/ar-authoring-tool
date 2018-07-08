package ncxp.de.mobiledatacollection.ui.study.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.ui.study.viewholder.ConfigViewHolder;
import ncxp.de.mobiledatacollection.ui.study.viewholder.SectionViewHolder;
import ncxp.de.mobiledatacollection.ui.study.viewholder.SensorSettingsViewHolder;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.OptionOthersListener;

public class OtherAdapter extends RecyclerView.Adapter {

	private List<Object>         sectionedOtherOptions;
	private OptionOthersListener listener;

	public OtherAdapter(List<Object> sectionedOtherOptions, OptionOthersListener listener) {
		this.sectionedOtherOptions = sectionedOtherOptions;
		this.listener = listener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder viewHolder = null;
		switch (viewType) {
			case R.layout.item_section:
				viewHolder = new SectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false));
				break;
			case R.layout.item_config:
				viewHolder = new ConfigViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_config, parent, false));
				break;
			case R.layout.item_sensor_options:
				viewHolder = new SensorSettingsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor_options, parent, false));
				break;
		}
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		switch (holder.getItemViewType()) {
			case R.layout.item_section:
				SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
				Integer id = (Integer) sectionedOtherOptions.get(position);
				sectionViewHolder.getSectionTitle().setText(id);
				break;
			case R.layout.item_config:
				ConfigViewHolder configViewHolder = (ConfigViewHolder) holder;
				OptionItem data = (OptionItem) sectionedOtherOptions.get(position);
				configViewHolder.getConfigName().setText(data.getName());
				configViewHolder.getConfigDescription().setText(data.getDescription());
				configViewHolder.getSwitchButton().setChecked(data.isActive());
				configViewHolder.getSwitchButton().setOnCheckedChangeListener((view, isChecked) -> data.setActive(isChecked));
				break;
			case R.layout.item_sensor_options:
				SensorSettingsViewHolder settingsViewHolder = (SensorSettingsViewHolder) holder;
				SensorSettings studySettings = (SensorSettings) sectionedOtherOptions.get(position);
				Button timeButton = settingsViewHolder.getTimeButton();
				timeButton.setText(studySettings.getSeconds() + "," + studySettings.getMilliseconds() + " s");
				timeButton.setOnClickListener((view) -> {
					listener.onTimePickerClicked(timeButton, studySettings);
				});

				break;
		}
	}

	@Override
	public int getItemViewType(int position) {
		Object item = sectionedOtherOptions.get(position);
		if (item instanceof Integer) {
			return R.layout.item_section;
		}
		if (item instanceof SensorSettings) {
			return R.layout.item_sensor_options;
		}
		if (item instanceof OptionItem) {
			return R.layout.item_config;
		}
		return position;
	}

	@Override
	public int getItemCount() {
		return sectionedOtherOptions != null ? sectionedOtherOptions.size() : 0;
	}

	public void addItems(List<Object> newSectionedDeviceSensors) {
		sectionedOtherOptions.addAll(newSectionedDeviceSensors);
		notifyDataSetChanged();
	}
}
