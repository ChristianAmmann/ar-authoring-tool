package ncxp.de.mobiledatacollection.ui.study.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.CapturingData;
import ncxp.de.mobiledatacollection.model.data.SensorSettings;
import ncxp.de.mobiledatacollection.ui.study.viewholder.ConfigViewHolder;
import ncxp.de.mobiledatacollection.ui.study.viewholder.SectionViewHolder;
import ncxp.de.mobiledatacollection.ui.study.viewholder.SensorSettingsViewHolder;

public class OtherAdapter extends RecyclerView.Adapter {

	private List<Object> sectionedOtherOptions;

	public OtherAdapter(List<Object> sectionedOtherOptions) {
		this.sectionedOtherOptions = sectionedOtherOptions;
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
				CapturingData data = (CapturingData) sectionedOtherOptions.get(position);
				configViewHolder.getConfigName().setText(data.getName());
				configViewHolder.getConfigDescription().setText(data.getDescription());
				configViewHolder.getSwitchButton().setChecked(data.isActive());
				configViewHolder.getSwitchButton().setOnCheckedChangeListener((view, isChecked) -> data.setActive(isChecked));
				break;
			case R.layout.item_sensor_options:
				SensorSettingsViewHolder settingsViewHolder = (SensorSettingsViewHolder) holder;
				SensorSettings sensorSettings = (SensorSettings) sectionedOtherOptions.get(position);
				String time = sensorSettings.getTimeInterval() + " s";
				settingsViewHolder.getTimeButton().setText(time);
				settingsViewHolder.getAccuracySeekBar().setProgress(sensorSettings.getAccuracy());
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
		if (item instanceof CapturingData) {
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
