package ncxp.de.mobiledatacollection.ui.study.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.ui.study.viewholder.ConfigViewHolder;
import ncxp.de.mobiledatacollection.ui.study.viewholder.SectionViewHolder;

public class SensorAdapter extends RecyclerView.Adapter {

	private List<Object> sectionedDeviceSensors;

	public SensorAdapter(List<Object> sectionedDeviceSensors) {
		this.sectionedDeviceSensors = sectionedDeviceSensors;
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
		}
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		switch (holder.getItemViewType()) {
			case R.layout.item_section:
				SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
				Integer id = (Integer) sectionedDeviceSensors.get(position);
				sectionViewHolder.getSectionTitle().setText(id);
				break;
			case R.layout.item_config:
				ConfigViewHolder configViewHolder = (ConfigViewHolder) holder;
				DeviceSensor deviceSensor = (DeviceSensor) sectionedDeviceSensors.get(position);
				configViewHolder.getConfigName().setText(deviceSensor.getType().getNameId());
				configViewHolder.getConfigDescription().setText(deviceSensor.getType().getDescriptionId());
				configViewHolder.getSwitchButton().setChecked(deviceSensor.isActive());
				configViewHolder.getSwitchButton().setOnClickListener((view) -> deviceSensor.setActive(configViewHolder.getSwitchButton().isChecked()));
				break;
		}
	}

	@Override
	public int getItemViewType(int position) {
		Object item = sectionedDeviceSensors.get(position);
		if (item instanceof DeviceSensor) {
			return R.layout.item_config;
		}
		if (item instanceof Integer) {
			return R.layout.item_section;
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

	public void replaceItems(List<Object> newSectionedDeviceSensors) {
		sectionedDeviceSensors = newSectionedDeviceSensors;
		notifyDataSetChanged();
	}
}
