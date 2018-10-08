package ncxp.de.arauthoringtool.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ncxp.de.arauthoringtool.model.data.DeviceSensor;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.StudyDeviceSensorJoin;
import ncxp.de.arauthoringtool.model.data.Survey;
import ncxp.de.arauthoringtool.repository.DeviceSensorRepository;
import ncxp.de.arauthoringtool.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.arauthoringtool.repository.StudyRepository;
import ncxp.de.arauthoringtool.repository.SurveyRepository;
import ncxp.de.arauthoringtool.sensorlogger.SensorDataManager;
import ncxp.de.arauthoringtool.sensorlogger.SensorGroup;
import ncxp.de.arauthoringtool.view.study.adapter.SensorSettings;

public class StudyViewModel extends ViewModel {

	private StudyRepository                 studyRepo;
	private SurveyRepository                surveyRepo;
	private DeviceSensorRepository          deviceRepo;
	private StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepo;
	private SensorDataManager               sensorDataManager;

	private Study                               study;
	private MutableLiveData<List<Survey>>       surveys;
	private MutableLiveData<List<DeviceSensor>> sensors;
	private SensorSettings                      settings;
	private boolean                             isTaskCompletionTimeActive;
	private boolean                             isAmountOfTouchEventsActive;
	private MutableLiveData<Boolean>            loading;

	public StudyViewModel(StudyRepository studyRepo,
						  SurveyRepository surveyRepo,
						  DeviceSensorRepository deviceRepo,
						  SensorDataManager sensorDataManager,
						  StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepo) {
		this.studyRepo = studyRepo;
		this.surveyRepo = surveyRepo;
		this.deviceRepo = deviceRepo;
		this.sensorDataManager = sensorDataManager;
		this.studyDeviceSensorJoinRepo = studyDeviceSensorJoinRepo;
		this.sensors = new MutableLiveData<>();
		this.surveys = new MutableLiveData<>();
		this.loading = new MutableLiveData<>();
	}


	public void setStudy(Study study) {
		this.study = study;
		this.isAmountOfTouchEventsActive = study.getAmountOfTouchEventsActive();
		this.isTaskCompletionTimeActive = study.getTaskCompletionTimeActive();
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

	public void createSurvey(String name, String description, String projectDirectory, String identifier) {
		Survey survey = new Survey();
		survey.setName(name);
		survey.setDescription(description);
		survey.setProjectDirectory(projectDirectory);
		survey.setIdentifier(identifier);
		List<Survey> newSurveys = surveys.getValue();
		if (newSurveys != null) {
			newSurveys.add(survey);
		} else {
			newSurveys = new ArrayList<>();
			newSurveys.add(survey);
		}
		surveys.postValue(newSurveys);
	}

	public void removeSurvey(Survey survey) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			if (survey.getId() != null) {
				surveyRepo.removeSurvey(survey);
			}
			List<Survey> value = surveys.getValue();
			value.remove(survey);
			surveys.postValue(value);
		});
	}

	public void save(String name, String description) {
		loading.postValue(true);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			Study study = new Study();
			study.setName(name);
			study.setDescription(description);
			study.setAccuracy(settings.getSensorAccuracy());
			study.setSamplingRate(settings.getSensorMeasuringDistance());
			study.setSamplingRate(settings.getSensorMeasuringDistance());
			study.setAccuracy(settings.getSensorAccuracy());
			study.setAmountOfTouchEventsActive(isAmountOfTouchEventsActive);
			study.setTaskCompletionTimeActive(isTaskCompletionTimeActive);
			long studyId = studyRepo.saveStudy(study);
			saveActiveDeviceSensors(studyId);
			saveSurveys(studyId);
			loading.postValue(false);
		});
	}

	public void update(Study updateStudy) {
		loading.postValue(true);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			updateStudy.setAmountOfTouchEventsActive(isAmountOfTouchEventsActive);
			updateStudy.setTaskCompletionTimeActive(isTaskCompletionTimeActive);
			studyRepo.updateStudy(updateStudy);
			updateActiveDeviceSensors(updateStudy);
			updateSurveys(updateStudy.getId());
			loading.postValue(false);
		});
	}

	private void updateActiveDeviceSensors(Study updateStudy) {
		sensors.getValue().stream().forEach(deviceSensor -> {
			deviceRepo.saveDeviceSensor(deviceSensor);
			StudyDeviceSensorJoin join = new StudyDeviceSensorJoin(updateStudy.getId(), deviceSensor.getName());
			if (!deviceSensor.isActive()) {
				studyDeviceSensorJoinRepo.removeStudyDeviceSensorJoin(join);
			} else {
				studyDeviceSensorJoinRepo.saveStudyDeviceSensorJoin(join);
			}
		});
	}

	private void saveActiveDeviceSensors(long studyId) {
		sensors.getValue().stream().filter(DeviceSensor::isActive).forEach(deviceSensor -> {
			deviceRepo.saveDeviceSensor(deviceSensor);
			StudyDeviceSensorJoin join = new StudyDeviceSensorJoin(studyId, deviceSensor.getName());
			studyDeviceSensorJoinRepo.saveStudyDeviceSensorJoin(join);
		});
	}

	private void saveSurveys(long studyId) {
		if (surveys.getValue() != null && !surveys.getValue().isEmpty()) {
			surveys.getValue().forEach(survey -> survey.setStudyId(studyId));
			surveyRepo.saveSurveys(surveys.getValue());
		}
	}

	private void updateSurveys(long studyId) {
		surveys.getValue().forEach(survey -> survey.setStudyId(studyId));
		surveyRepo.updateSurveys(surveys.getValue());
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

	public void setSensorSettings(SensorSettings settings) {
		this.settings = settings;
	}

	public SensorSettings getSensorSettings() {
		return settings;
	}

	public void setTaskCompletionTime(boolean active) {
		this.isTaskCompletionTimeActive = active;
	}

	public boolean isTaskCompletionTimeActive() {
		return isTaskCompletionTimeActive;
	}

	public boolean isAmountOfTouchEventsActive() {
		return isAmountOfTouchEventsActive;
	}

	public void setAmountOfTouchEvents(boolean active) {
		this.isAmountOfTouchEventsActive = active;
	}

	public LiveData<Boolean> isLoading() {
		return this.loading;
	}
}
