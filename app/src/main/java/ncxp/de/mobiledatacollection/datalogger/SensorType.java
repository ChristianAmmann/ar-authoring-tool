package ncxp.de.mobiledatacollection.datalogger;

import android.hardware.Sensor;

import java.util.HashMap;
import java.util.Map;

import ncxp.de.mobiledatacollection.R;

public enum SensorType {

	ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, R.string.accelerometer, R.string.accelerometer_description, SensorGroup.MOTION),
	ACCELEROMETER_UNCALIBRATED(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, R.string.accelerometer_uncalibrated, R.string.accelerometer_uncalibrated_description, SensorGroup.MOTION),
	GRAVITY(Sensor.TYPE_GRAVITY, R.string.gravity, R.string.gravity_description, SensorGroup.MOTION),
	GYROSCOPE(Sensor.TYPE_GYROSCOPE, R.string.gyroscope, R.string.gyroscope_description, SensorGroup.MOTION),
	GYROSCOPE_UNCALIBRATED(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, R.string.gyroscope_uncalibrated, R.string.gyroscope_uncalibrated_description, SensorGroup.MOTION),
	LINEAR_ACCELEROMETER(Sensor.TYPE_LINEAR_ACCELERATION, R.string.linear_accelerometer, R.string.linear_accelerometer_description, SensorGroup.MOTION),
	STEP_COUNTER(Sensor.TYPE_STEP_COUNTER, R.string.step_counter, R.string.step_counter_description, SensorGroup.MOTION),
	ROTATION_VECTOR(Sensor.TYPE_ROTATION_VECTOR, R.string.rotation_vector, R.string.rotation_vector_description, SensorGroup.MOTION);

	private static Map<Integer, SensorType> map = new HashMap<>();

	static {
		for (SensorType type : SensorType.values()) {
			map.put(type.sensorType, type);
		}
	}

	private int         sensorType;
	private int         nameId;
	private int         descriptionId;
	private SensorGroup group;

	SensorType(int sensorType, int nameId, int descriptionId, SensorGroup group) {
		this.sensorType = sensorType;
		this.nameId = nameId;
		this.descriptionId = descriptionId;
		this.group = group;
	}

	public static SensorType getSensorType(int sensorType) {
		return map.get(sensorType);
	}

	public int getSensorType() {
		return sensorType;
	}

	public int getNameId() {
		return nameId;
	}

	public int getDescriptionId() {
		return descriptionId;
	}

	public SensorGroup getGroup() {
		return group;
	}
}
