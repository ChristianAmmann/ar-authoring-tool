package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import java.io.Serializable;

import ncxp.de.mobiledatacollection.datalogger.SensorType;

@Entity(tableName = DeviceSensor.TABLE_NAME)
public class DeviceSensor implements Serializable {

	public static final String TABLE_NAME = "DeviceSensor";

	@PrimaryKey
	@NonNull
	private String     name;
	@Ignore
	private Sensor     sensor;
	@Ignore
	private SensorType type;
	@Ignore
	private boolean    active;


	public DeviceSensor() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
		this.type = SensorType.getSensorType(sensor.getType());
		this.name = sensor.getName();
	}

	public SensorType getType() {
		return type;
	}

	public void setType(SensorType type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
