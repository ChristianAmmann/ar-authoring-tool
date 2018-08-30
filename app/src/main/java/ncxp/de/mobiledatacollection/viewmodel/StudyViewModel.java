package ncxp.de.mobiledatacollection.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.datalogger.SensorGroup;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Measurement;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.StudyDeviceSensorJoin;
import ncxp.de.mobiledatacollection.model.data.StudyMeasurementJoin;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.repository.DeviceSensorRepository;
import ncxp.de.mobiledatacollection.model.repository.MeasurementRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyMeasurementJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.ui.study.adapter.SensorSettings;

public class StudyViewModel extends ViewModel {

	private StudyRepository                 studyRepo;
	private SurveyRepository                surveyRepo;
	private DeviceSensorRepository          deviceRepo;
	private StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepo;
	private MeasurementRepository           measurementRepo;
	private StudyMeasurementJoinRepository  studyMeasurementJoinRepo;
	private SensorDataManager               sensorDataManager;

	private Study                               study;
	private MutableLiveData<List<Survey>>       surveys;
	private MutableLiveData<List<DeviceSensor>> sensors;
	private MutableLiveData<List<Measurement>>  measurements;
	private SensorSettings                      settings;
	private boolean                             isCapturingScreen;
	private boolean                             isCapturingAudio;

	public StudyViewModel(StudyRepository studyRepo,
						  SurveyRepository surveyRepo,
						  DeviceSensorRepository deviceRepo,
						  SensorDataManager sensorDataManager,
						  StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepo,
						  MeasurementRepository measurementRepo,
						  StudyMeasurementJoinRepository studyMeasurementJoinRepo) {
		this.studyRepo = studyRepo;
		this.surveyRepo = surveyRepo;
		this.deviceRepo = deviceRepo;
		this.sensorDataManager = sensorDataManager;
		this.studyDeviceSensorJoinRepo = studyDeviceSensorJoinRepo;
		this.studyMeasurementJoinRepo = studyMeasurementJoinRepo;
		this.measurementRepo = measurementRepo;
		this.sensors = new MutableLiveData<>();
		this.surveys = new MutableLiveData<>();
		this.measurements = new MutableLiveData<>();
	}


	public void setStudy(Study study) {
		this.study = study;
		this.isCapturingAudio = study.isCapturingAudio();
		this.isCapturingScreen = study.isCapturingScreen();
	}

	public Study getStudy() {
		return this.study;
	}

	public void init() {
		if (study != null) {
			settings = new SensorSettings();
			settings.setSensorAccuracy(study.getAccuracy());
			settings.setSensorMeasuringDistance(study.getSamplingRate());
			loadSurveys();
			loadActiveSensors();
			loadMeasurements();
		} else {
			settings = new SensorSettings();
			initAvailableDeviceSensor(null);
		}
	}

	public LiveData<List<Survey>> getSurveys() {
		return surveys;
	}

	private void loadSurveys() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<Survey> surveysFromStudy = surveyRepo.getSurveysFromStudy(study);
			surveys.postValue(surveysFromStudy);
		});
	}

	public LiveData<List<DeviceSensor>> getSensors() {
		return sensors;
	}

	private void loadActiveSensors() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<DeviceSensor> deviceSensorsForStudy = studyDeviceSensorJoinRepo.getDeviceSensorsForStudy(study);
			initAvailableDeviceSensor(deviceSensorsForStudy);
		});
	}

	private void initAvailableDeviceSensor(List<DeviceSensor> activeSensors) {
		List<DeviceSensor> availableDeviceSensors = new ArrayList<>();
		sensorDataManager.getAvailableDeviceSensors().forEach(sensor -> {
			DeviceSensor deviceSensor = new DeviceSensor();
			deviceSensor.setSensor(sensor);
			if (activeSensors != null && !activeSensors.isEmpty()) {
				activeSensors.stream().filter(savedSensor -> savedSensor.getName().equals(deviceSensor.getName())).forEach(savedSensors -> deviceSensor.setActive(true));
			}
			if (deviceSensor.getSensorType() != null) {
				availableDeviceSensors.add(deviceSensor);
			}
		});
		sensors.postValue(availableDeviceSensors);
	}

	public LiveData<List<Measurement>> getMeasurements() {
		return measurements;
	}

	private void loadMeasurements() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<Measurement> savedMeasurements = studyMeasurementJoinRepo.getMeasurementsForStudy(study);
			measurements.postValue(savedMeasurements);
		});
	}

	public void createMeasurement(String name) {
		Measurement measurement = new Measurement();
		measurement.setName(name);
		List<Measurement> newMeasurements = new ArrayList<>();
		if (measurements.getValue() != null) {
			newMeasurements = measurements.getValue();
		}
		newMeasurements.add(measurement);
		measurements.postValue(newMeasurements);
	}

	public void removeMeasurement(String name) {
		Measurement measurement = new Measurement();
		measurement.setName(name);
		if (measurements.getValue() != null) {
			measurements.getValue().remove(measurement);
		}
	}

	public void createSurvey(String name, String description, String projectDirectory, String identifier) {
		Survey survey = new Survey();
		survey.setName(name);
		survey.setDescription(description);
		survey.setProjectDirectory(projectDirectory);
		survey.setIdentifier(identifier);
		List<Survey> newSurveys = new ArrayList<>();
		newSurveys.add(survey);
		surveys.postValue(newSurveys);
	}

	public void removeSurvey(Survey survey) {
		surveys.getValue().remove(survey);
	}

	public void save(String name, String description) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			Study study = new Study();
			study.setName(name);
			study.setDescription(description);
			study.setAccuracy(settings.getSensorAccuracy());
			study.setSamplingRate(settings.getSensorMeasuringDistance());
			study.setCapturingScreen(isCapturingScreen);
			study.setCapturingAudio(isCapturingAudio);
			study.setSamplingRate(settings.getSensorMeasuringDistance());
			study.setAccuracy(settings.getSensorAccuracy());
			long studyId = saveStudy(study);
			saveActiveDeviceSensors(studyId);
			saveSurveys(studyId);
			saveMeasurements(studyId);
		});
	}

	public void update(Study updateStudy) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			study.setCapturingAudio(isCapturingAudio);
			study.setCapturingScreen(isCapturingScreen);
			updateStudy(updateStudy);
			saveActiveDeviceSensors(updateStudy.getId());
			saveSurveys(updateStudy.getId());
			saveMeasurements(updateStudy.getId());
		});
	}

	private void updateStudy(Study study) {
		studyRepo.saveStudy(study);
	}

	private long saveStudy(Study study) {
		return studyRepo.saveStudy(study);
	}

	private void saveActiveDeviceSensors(long studyId) {
		sensors.getValue().stream().filter(DeviceSensor::isActive).forEach(deviceSensor -> {
			deviceRepo.saveDeviceSensor(deviceSensor);
			StudyDeviceSensorJoin join = new StudyDeviceSensorJoin(studyId, deviceSensor.getName());
			studyDeviceSensorJoinRepo.saveStudyDeviceSensorJoin(join);
		});
	}

	private void saveSurveys(long studyId) {
		surveys.getValue().forEach(survey -> survey.setStudyId(studyId));
		surveyRepo.saveSurveys(surveys.getValue());
	}

	private void saveMeasurements(long studyId) {
		measurements.getValue().forEach(measurement -> {
			long measurementId = measurementRepo.saveMeasurement(measurement);
			StudyMeasurementJoin join = new StudyMeasurementJoin(studyId, measurementId);
			studyMeasurementJoinRepo.saveStudyMeasurementJoin(join);
		});
	}

	public List<Object> getSectionedDeviceSensors(List<DeviceSensor> deviceSensors) {
		List<Object> sectionedDeviceSensors = new ArrayList<>();
		for (SensorGroup group : SensorGroup.values()) {
			List<DeviceSensor> sensorsOfGroup = deviceSensors.stream()
															 .filter(availableDeviceSensor -> availableDeviceSensor.getSensorType().getGroup().equals(group))
															 .collect(Collectors.toList());
			sectionedDeviceSensors.add(group.getGroupId());
			sectionedDeviceSensors.addAll(sensorsOfGroup);
		}
		return sectionedDeviceSensors;
	}

	public void setCapturingScreen(boolean capturingScreen) {
		isCapturingScreen = capturingScreen;
	}

	public void setCapturingAudio(boolean capturingAudio) {
		isCapturingAudio = capturingAudio;
	}

	public void setSensorSettings(SensorSettings settings) {
		this.settings = settings;
	}

	public SensorSettings getSensorSettings() {
		return settings;
	}

	public boolean isCapturingScreen() {
		return isCapturingScreen;
	}

	public boolean isCapturingAudio() {
		return isCapturingAudio;
	}

	public boolean isMeasurementActive(String name) {
		if (measurements.getValue() != null) {
			return measurements.getValue().stream().anyMatch(measurement -> measurement.getName().equals(name));
		}
		return false;
	}
}