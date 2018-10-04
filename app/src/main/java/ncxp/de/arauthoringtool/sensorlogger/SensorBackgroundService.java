package ncxp.de.arauthoringtool.sensorlogger;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ncxp.de.arauthoringtool.model.StudyDatabase;
import ncxp.de.arauthoringtool.model.dao.DataDao;
import ncxp.de.arauthoringtool.model.dao.TestPersonDao;
import ncxp.de.arauthoringtool.model.data.Data;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.TestPerson;
import ncxp.de.arauthoringtool.model.repository.DataRepository;
import ncxp.de.arauthoringtool.model.repository.TestPersonRepository;

public class SensorBackgroundService extends Service implements SensorEventListener {

	private static final String               TAG    = SensorBackgroundService.class.getSimpleName();
	private final        IBinder              binder = new SensorBackgroundBinder();
	private              long                 sensorTimeReference;
	private              SensorManager        sensorManager;
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
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
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

	private void save(String sensorName, int accuracy, long timestamp, float[] values) {
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
			study.getSensors().forEach(deviceSensor -> {
				List<Sensor> sensors = sensorManager.getSensorList(deviceSensor.getType());
				sensors.stream()
					   .filter(sensor -> !sensor.isWakeUpSensor())
					   .forEach(sensor -> sensorManager.registerListener(this, sensor, ((int) study.getSamplingRate().doubleValue() * 1000000)));
			});
		}
	}

	private void stopCollecting() {
		sensorManager.unregisterListener(this);
	}

	public void initialize(Study study) {
		this.study = study;
		this.person = study.getCurrentSubject();
		sensorTimeReference = 0L;
	}


	public void start() {
		startCollecting();
	}


	public void stop() {
		stopCollecting();
	}

	public void abort() {
		stopCollecting();
		deleteTestPersonWithData();
	}

	private void deleteTestPersonWithData() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			testPersonRepository.removeTestPerson(person);
			person = null;
		});
	}


	private String arrayToString(float[] array) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			result.append(array[i]).append(";");
		}
		return result.toString();
	}

	public class SensorBackgroundBinder extends Binder {
		public SensorBackgroundService getService() {
			return SensorBackgroundService.this;
		}
	}
}
