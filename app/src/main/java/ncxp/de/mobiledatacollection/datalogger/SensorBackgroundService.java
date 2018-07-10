package ncxp.de.mobiledatacollection.datalogger;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ncxp.de.mobiledatacollection.model.data.Study;

public class SensorBackgroundService extends Service implements SensorEventListener {

	/**
	 * a tag for logging
	 */
	private static final String TAG = SensorBackgroundService.class.getSimpleName();

	/**
	 * again we need the sensor manager and sensor reference
	 */
	private SensorManager sensorManager = null;

	/**
	 * an optional flag for logging
	 */
	private boolean logging = false;

	/**
	 * also keep track of the previous value
	 */
	private static float previousValue;

	/**
	 * treshold values
	 */
	private float mThresholdMin, mThresholdMax;
	public static final String KEY_STUDY = "study_";

	private Study study;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Bundle args = intent.getExtras();
		if (args != null) {
			if (args.containsKey(KEY_STUDY)) {
				study = args.getParcelable(KEY_STUDY);
			}
		}

		if (study != null && study.getSensors() != null) {
			study.getSensors().stream().forEach(deviceSensor -> {
				List<Sensor> sensors = sensorManager.getSensorList(deviceSensor.getType());
				sensors.forEach(sensor -> sensorManager.registerListener(this, sensor, ((int) study.getSensorMeasuringDistance() * 1000000)));
			});
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// ignore this since not linked to an activity
		return null;
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			//TODO do something
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
			Date date = new Date(event.timestamp);
			String timpstamp = format.format(date);
			Log.d(TAG, "Sensor: " + event.sensor.getName() + " Genauigkeit: " + event.accuracy + " Timestamp: " + timpstamp + " Values: " + event.values.toString());
		} else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			//TODO do something
		}
	}
}
