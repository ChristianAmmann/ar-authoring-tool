package ncxp.de.mobiledatacollection.ui.study.viewmodel;

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
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.StudyDeviceSensorJoin;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.repository.DeviceSensorRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;

public class StudyViewModel extends ViewModel {

	private StudyRepository                 studyRepo;
	private SurveyRepository                surveyRepo;
	private DeviceSensorRepository          deviceRepo;
	private StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepo;
	private SensorDataManager               sensorDataManager;

	private Study                               study;
	private MutableLiveData<List<Survey>>       surveys;
	private MutableLiveData<List<DeviceSensor>> sensors;

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
	}


	public void setStudy(Study study) {
		this.study = study;
	}

	public Study getStudy() {
		return this.study;
	}

	public void init() {
		if (study != null) {
			loadSurveys();
			loadActiveSensors();
		} else {
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

	private void loadActiveSensors() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<DeviceSensor> deviceSensorsForStudy = studyDeviceSensorJoinRepo.getDeviceSensorsForStudy(study);
			initAvailableDeviceSensor(deviceSensorsForStudy);
		});
	}

	public LiveData<List<DeviceSensor>> getSensors() {
		return sensors;
	}

	private void initAvailableDeviceSensor(List<DeviceSensor> activeSensors) {
		List<DeviceSensor> availableDeviceSensors = new ArrayList<>();
		sensorDataManager.getAvailableDeviceSensors().forEach(sensor -> {
			DeviceSensor deviceSensor = new DeviceSensor();
			deviceSensor.setSensor(sensor);
			if (activeSensors != null && !activeSensors.isEmpty()) {
				activeSensors.stream().filter(savedSensor -> savedSensor.getName().equals(deviceSensor.getName())).forEach(savedSensors -> deviceSensor.setActive(true));
			}
			if (deviceSensor.getType() != null) {
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
			long studyId = saveStudy(name, description);
			saveActiveDeviceSensors(studyId);
			saveSurveys(studyId);
		});
	}

	public void update(Study updateStudy) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			updateStudy(updateStudy);
			saveActiveDeviceSensors(updateStudy.getId());
			saveSurveys(updateStudy.getId());
		});
	}

	private void updateStudy(Study study) {
		studyRepo.saveStudy(study);
	}

	private long saveStudy(String name, String description) {
		Study study = new Study();
		study.setName(name);
		study.setDescription(description);
		return studyRepo.saveStudy(study);
	}

	private void saveActiveDeviceSensors(long studyId) {
		sensors.getValue().stream().filter(DeviceSensor::isActive).forEach(deviceSensor -> {
			long deviceId = deviceRepo.saveDeviceSensor(deviceSensor);
			StudyDeviceSensorJoin join = new StudyDeviceSensorJoin(studyId, deviceId);
			studyDeviceSensorJoinRepo.saveStudyDeviceSensorJoin(join);
		});
	}

	private void saveSurveys(long studyId) {
		surveys.getValue().forEach(survey -> survey.setStudyId(studyId));
		surveyRepo.saveSurveys(surveys.getValue());
	}

	public List<Object> getSectionedDeviceSensors(List<DeviceSensor> deviceSensors) {
		List<Object> sectionedDeviceSensors = new ArrayList<>();
		for (SensorGroup group : SensorGroup.values()) {
			List<DeviceSensor> sensorsOfGroup = deviceSensors.stream()
															 .filter(availableDeviceSensor -> availableDeviceSensor.getType().getGroup().equals(group))
															 .collect(Collectors.toList());
			sectionedDeviceSensors.add(group.getGroupId());
			sectionedDeviceSensors.addAll(sensorsOfGroup);
		}
		return sectionedDeviceSensors;
	}
}
