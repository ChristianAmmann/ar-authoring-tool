package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.hardware.Sensor;

import java.io.Serializable;

@Entity(tableName = DeviceSensor.TABLE_NAME)
public class DeviceSensor implements Serializable {

	public static final String TABLE_NAME = "DeviceSensor";

	@PrimaryKey
	private final long   id;
	private       String name;
	private       String description;
	@Ignore
	private       Sensor sensor;


	public DeviceSensor(long id, String name, String description, Sensor sensor, boolean active) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.sensor = sensor;
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
}
