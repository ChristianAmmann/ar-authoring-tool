package ncxp.de.mobiledatacollection.ui.study.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.datalogger.sensor.DeviceSensor;

public class SensorAdapter extends RecyclerView.Adapter<ConfigViewHolder> {

	private List<DeviceSensor> sensors;

	public SensorAdapter(ArrayList<DeviceSensor> deviceSensors) {
		this.sensors = deviceSensors;
	}

	@NonNull
	@Override
	public ConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.config_item, parent, false);
		return new ConfigViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ConfigViewHolder holder, int position) {
		DeviceSensor deviceSensor = sensors.get(position);
		holder.getConfigName().setText(deviceSensor.getName());
		holder.getConfigDescription().setText(deviceSensor.getDescription());
		holder.getSwitchButton().setChecked(deviceSensor.isActive());
		holder.getSwitchButton().setOnCheckedChangeListener((view, isChecked) -> deviceSensor.setActive(isChecked));
	}

	@Override
	public int getItemCount() {
		return sensors != null ? sensors.size() : 0;
	}

	public void addItems(List<DeviceSensor> newSensors) {
		sensors.addAll(newSensors);
		notifyDataSetChanged();
	}


}
