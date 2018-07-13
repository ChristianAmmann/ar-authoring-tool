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
import ncxp.de.mobiledatacollection.model.dao.TestPersonDao;
import ncxp.de.mobiledatacollection.model.data.Data;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.TestPerson;
import ncxp.de.mobiledatacollection.model.repository.DataRepository;
import ncxp.de.mobiledatacollection.model.repository.TestPersonRepository;

public class SensorBackgroundService extends Service implements SensorEventListener {

	private static final String               TAG                 = SensorBackgroundService.class.getSimpleName();
	private              long                 sensorTimeReference = 0l;
	private              SensorManager        sensorManager       = null;
	public static final  String               KEY_STUDY           = "study_";
	private              Study                study;
	private              TestPerson           person;
	private              DataRepository       dataRepository;
	private              TestPersonRepository testPersonRepository;

	@Override
	public void onCreate() {
		super.onCreate();
		StudyDatabase database = StudyDatabase.getInstance(getApplicationContext());
		DataDao dataDao = database.dataDao();
		TestPersonDao personDao = database.testPerson();
		dataRepository = new DataRepository(dataDao);
		testPersonRepository = new TestPersonRepository(personDao);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Log.d(TAG, "onStart");
		if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(KEY_STUDY)) {
			study = intent.getExtras().getParcelable(KEY_STUDY);
			createTestperson();
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
		save(event.sensor.getName(), event.accuracy, timestamp, event.values);
	}

	public void save(String sensorName, int accuracy, long timestamp, float[] values) {
		if (accuracy < study.getAccuracy()) {
			return;
		}
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			Data data = new Data();
			data.setSource(sensorName);
			data.setTestPersonId(person.getId());
			data.setTimestamp(timestamp);
			data.setValues(arrayToString(values));
			dataRepository.saveData(data);
		});
	}

	private void startCollecting() {
		if (study != null && study.getSensors() != null) {
			study.getSensors().stream().forEach(deviceSensor -> {
				List<Sensor> sensors = sensorManager.getSensorList(deviceSensor.getType());
				sensors.stream()
					   .filter(sensor -> !sensor.isWakeUpSensor())
					   .forEach(sensor -> sensorManager.registerListener(this, sensor, ((int) study.getSamplingRate().doubleValue() * 1000000)));
			});
		}
	}

	private void createTestperson() {
		Log.d(TAG, "createTestPerson");
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			person = new TestPerson();
			person.setStudyId(study.getId());
			long id = testPersonRepository.saveTestPerson(person);
			person.setId(id);
			Log.d(TAG, "" + id);
			startCollecting();
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
