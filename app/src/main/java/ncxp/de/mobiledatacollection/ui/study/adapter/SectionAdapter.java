package ncxp.de.mobiledatacollection.ui.study.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.datalogger.AvailableDeviceSensor;

public class SectionAdapter extends RecyclerView.Adapter {

	private List<Object> sectionedDeviceSensors;

	public SectionAdapter(List<Object> sectionedDeviceSensors) {
		this.sectionedDeviceSensors = sectionedDeviceSensors;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder viewHolder = null;
		switch (viewType) {
			case R.layout.section:
				viewHolder = new SectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false));
				break;
			case R.layout.config_item:
				viewHolder = new ConfigViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.config_item, parent, false));
				break;
		}
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		switch (holder.getItemViewType()) {
			case R.layout.section:
				SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
				Integer id = (Integer) sectionedDeviceSensors.get(position);
				sectionViewHolder.getSectionTitle().setText(id);
				break;
			case R.layout.config_item:
				ConfigViewHolder configViewHolder = (ConfigViewHolder) holder;
				AvailableDeviceSensor deviceSensor = (AvailableDeviceSensor) sectionedDeviceSensors.get(position);
				configViewHolder.getConfigName().setText(deviceSensor.getNameId());
				configViewHolder.getConfigDescription().setText(deviceSensor.getDescriptionId());
				configViewHolder.getSwitchButton().setChecked(deviceSensor.isActive());
				configViewHolder.getSwitchButton().setOnCheckedChangeListener((view, isChecked) -> deviceSensor.setActive(isChecked));
				break;
		}
	}

	@Override
	public int getItemViewType(int position) {
		Object item = sectionedDeviceSensors.get(position);
		if (item instanceof AvailableDeviceSensor) {
			return R.layout.config_item;
		}
		if (item instanceof Integer) {
			return R.layout.section;
		}
		return position;
	}

	@Override
	public int getItemCount() {
		return sectionedDeviceSensors != null ? sectionedDeviceSensors.size() : 0;
	}

	public void addItems(List<Object> newSectionedDeviceSensors) {
		sectionedDeviceSensors.addAll(newSectionedDeviceSensors);
		notifyDataSetChanged();
	}
}
