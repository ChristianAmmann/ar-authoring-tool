package ncxp.de.mobiledatacollection.datalogger;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;
import java.util.stream.Collectors;

public class SensorDataManager {

	private static final String TAG = SensorDataManager.class.getSimpleName();

	private SensorManager sensorManager;


	private static SensorDataManager instance;

	public static SensorDataManager getInstance(Context context) {
		if (instance == null) {
			instance = new SensorDataManager(context);
		}
		return instance;
	}

	private SensorDataManager(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

	}

	public List<Sensor> getAvailableDeviceSensors() {
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
		sensorList = sensorList.stream().filter(sensor -> !sensor.isWakeUpSensor()).collect(Collectors.toList());
		return sensorList;
	}
}
