package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import java.io.Serializable;

import ncxp.de.mobiledatacollection.sensorlogger.SensorType;

@Entity(tableName = DeviceSensor.TABLE_NAME)
public class DeviceSensor implements Serializable {

	public static final String TABLE_NAME  = "DeviceSensor";
	public static final String COLUMN_ID   = "id";
	public static final String COLUMN_TYPE = "sensor_type";

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = COLUMN_ID)
	private String     name;
	@ColumnInfo(name = COLUMN_TYPE)
	private int        type;
	@Ignore
	private Sensor     sensor;
	@Ignore
	private SensorType sensorType;
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
		this.sensorType = SensorType.getSensorType(sensor.getType());
		this.name = sensor.getName();
		this.type = sensor.getType();
	}

	public SensorType getSensorType() {
		return sensorType;
	}

	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
