package ncxp.de.arauthoringtool.sensorlogger;

import android.hardware.Sensor;

import java.util.HashMap;
import java.util.Map;

import ncxp.de.arauthoringtool.R;

public enum SensorType {

	ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, R.string.accelerometer, R.string.accelerometer_description, SensorGroup.MOTION),
	GRAVITY(Sensor.TYPE_GRAVITY, R.string.gravity, R.string.gravity_description, SensorGroup.MOTION),
	GYROSCOPE(Sensor.TYPE_GYROSCOPE, R.string.gyroscope, R.string.gyroscope_description, SensorGroup.MOTION),
	LINEAR_ACCELEROMETER(Sensor.TYPE_LINEAR_ACCELERATION, R.string.linear_accelerometer, R.string.linear_accelerometer_description, SensorGroup.MOTION),
	STEP_COUNTER(Sensor.TYPE_STEP_COUNTER, R.string.step_counter, R.string.step_counter_description, SensorGroup.MOTION),
	ROTATION_VECTOR(Sensor.TYPE_ROTATION_VECTOR, R.string.rotation_vector, R.string.rotation_vector_description, SensorGroup.MOTION),
	GAME_ROTATION_VECTOR(Sensor.TYPE_GAME_ROTATION_VECTOR, R.string.game_rotation_vector, R.string.rotation_vector_description, SensorGroup.POSITION),
	GEOMAGNETIC_ROTATION_VECTOR(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
								R.string.geomagnetic_rotation_vector,
								R.string.geomagnetic_rotation_vector_description,
								SensorGroup.POSITION),
	MAGNETIC_FIELD(Sensor.TYPE_MAGNETIC_FIELD, R.string.magnetic_field, R.string.magnetic_field_description, SensorGroup.POSITION),
	ORIENTATION(Sensor.TYPE_ORIENTATION, R.string.orientation, R.string.orientation_description, SensorGroup.POSITION),
	PROXIMITY(Sensor.TYPE_PROXIMITY, R.string.proximity, R.string.proximity_description, SensorGroup.POSITION),
	AMBIENT_TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE, R.string.ambient_temperature, R.string.ambient_temperature_description, SensorGroup.ENVIROMENT),
	LIGHT(Sensor.TYPE_LIGHT, R.string.light, R.string.light_description, SensorGroup.ENVIROMENT),
	PRESSURE(Sensor.TYPE_PRESSURE, R.string.pressure, R.string.pressure_description, SensorGroup.ENVIROMENT),
	TEMPERATURE(Sensor.TYPE_TEMPERATURE, R.string.temperature, R.string.temperature_description, SensorGroup.ENVIROMENT);


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
