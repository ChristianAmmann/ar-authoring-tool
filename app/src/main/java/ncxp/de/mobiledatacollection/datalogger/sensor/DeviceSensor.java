package ncxp.de.mobiledatacollection.datalogger.sensor;

import android.hardware.Sensor;

import java.io.Serializable;

public class DeviceSensor implements Serializable {

	private String  name;
	private String  description;
	private Sensor  sensor;
	private boolean active = false;

	public DeviceSensor(Sensor sensor) {
		this.sensor = sensor;
		//TODO Enum for name and description and Grouping
		this.name = sensor.getName();
		this.description = sensor.getStringType();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
