package ncxp.de.mobiledatacollection.datalogger;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.DataDao;
import ncxp.de.mobiledatacollection.model.data.Data;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.TestPerson;
import ncxp.de.mobiledatacollection.model.repository.DataRepository;

public class SensorBackgroundService extends Service implements SensorEventListener {

	private static final String         TAG                 = SensorBackgroundService.class.getSimpleName();
	private              long           sensorTimeReference = 0l;
	private              SensorManager  sensorManager       = null;
	public static final  String         KEY_STUDY           = "study_";
	private              Study          study;
	private              TestPerson     person;
	private              DataRepository dataRepository;

	@Override
	public void onCreate() {
		super.onCreate();
		DataDao dataDao = StudyDatabase.getInstance(getApplicationContext()).dataDao();
		dataRepository = new DataRepository(dataDao);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		if (intent.getExtras() != null && intent.getExtras().containsKey(KEY_STUDY)) {
			study = intent.getExtras().getParcelable(KEY_STUDY);
			//TODO Real testperson
			person = new TestPerson(1, study.getId());
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
		if (sensorTimeReference == 0L) {
			sensorTimeReference = event.timestamp;
		}
		long timestamp = event.timestamp - sensorTimeReference;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			//TODO Save data
			//Log.d(TAG, "Sensor: " + event.sensor.getName() + " ACC: " + event.accuracy + " Time: " + timestamp + " " + arrayToString(event.values));
			save(event.sensor.getName(), event.accuracy, timestamp, event.values);
		} else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			//TODO do something
		}
	}

	public void save(String sensorName, int accuracy, long timestamp, float[] values) {
		//TODO Accuracy filter or save database
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			//TODO fix persistence
			Data data = new Data();
			data.setSource(sensorName);
			data.setTestPersonId(person.getId());
			data.setTimestamp(timestamp);
			data.setValues(arrayToString(values));
			dataRepository.saveData(data);
			Log.d(TAG, data.toString());
		});

	}


	private String arrayToString(float[] array) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			result.append(array[i]).append(";");
		}
		return result.toString();
	}
}
