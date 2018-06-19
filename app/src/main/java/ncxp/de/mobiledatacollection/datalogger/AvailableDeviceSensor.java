package ncxp.de.mobiledatacollection.datalogger;

import android.hardware.Sensor;

public class AvailableDeviceSensor {

	private Sensor     sensor;
	private SensorType type;
	private boolean    active = false;

	public AvailableDeviceSensor(Sensor sensor) {
		this.sensor = sensor;
		this.type = SensorType.getSensorType(sensor.getType());
	}

	public int getNameId() {
		return type.getNameId();
	}

	public int getDescriptionId() {
		return type.getDescriptionId();
	}

	public Sensor getSensor() {
		return sensor;
	}

	public SensorType getType() {
		return type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
