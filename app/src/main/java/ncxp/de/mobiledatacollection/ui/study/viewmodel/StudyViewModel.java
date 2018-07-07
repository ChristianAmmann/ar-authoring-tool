package ncxp.de.mobiledatacollection.ui.study.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
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

	private StudyRepository                      studyRepo;
	private SurveyRepository                     surveyRepo;
	private DeviceSensorRepository               deviceRepo;
	private StudyDeviceSensorJoinRepository      studyDeviceSensorJoinRepo;
	private SensorDataManager                    sensorDataManager;
	private Study                                study;
	private MediatorLiveData<List<Survey>>       savedSurveys;
	private MutableLiveData<List<DeviceSensor>>  availableSensors;
	private MediatorLiveData<List<DeviceSensor>> savedSensors;

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
		this.availableSensors = new MutableLiveData<>();
		this.savedSurveys = new MediatorLiveData<>();
		this.savedSensors = new MediatorLiveData<>();
		availableSensors.setValue(new ArrayList<>());
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
		}
		loadActiveSensors();
		initAvailableDeviceSensor();
	}

	public LiveData<List<Survey>> getSavedSurveys() {
		return this.savedSurveys;
	}

	private void loadSurveys() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			LiveData<List<Survey>> surveysFromStudy = surveyRepo.getSurveysFromStudy(study);
			savedSurveys.addSource(surveysFromStudy, (surveys) -> {
				if (surveys != null) {
					savedSurveys.removeSource(surveysFromStudy);
					savedSurveys.setValue(surveys);
				}
			});
		});
	}

	private void loadActiveSensors() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			LiveData<List<DeviceSensor>> deviceSensorsForStudy = studyDeviceSensorJoinRepo.getDeviceSensorsForStudy(study);
			savedSensors.addSource(deviceSensorsForStudy, (deviceSensors -> {
				if (deviceSensors != null) {
					savedSensors.removeSource(deviceSensorsForStudy);
					savedSensors.setValue(deviceSensors);
					initAvailableDeviceSensor();
				}
			}));
		});
	}

	public LiveData<List<DeviceSensor>> getActiveDeviceSensor() {
		return this.savedSensors;
	}

	public MutableLiveData<List<DeviceSensor>> getAvailableSensors() {
		return availableSensors;
	}

	private void initAvailableDeviceSensor() {
		List<DeviceSensor> availableDeviceSensors = new ArrayList<>();
		sensorDataManager.getAvailableDeviceSensors().forEach(sensor -> {
			DeviceSensor deviceSensor = new DeviceSensor();
			deviceSensor.setSensor(sensor);
			if (savedSensors.getValue() != null) {
				savedSensors.getValue().stream().filter(savedSensor -> savedSensor.getName().equals(deviceSensor.getName())).forEach(savedSensors -> deviceSensor.setActive(true));
			}
			if (deviceSensor.getType() != null) {
				availableDeviceSensors.add(deviceSensor);
			}
		});
		availableSensors.setValue(availableDeviceSensors);
	}

	public void createSurvey(String name, String description, String projectDirectory, String identifier) {
		Survey survey = new Survey();
		survey.setName(name);
		survey.setDescription(description);
		survey.setProjectDirectory(projectDirectory);
		survey.setIdentifier(identifier);
		List<Survey> surveys = new ArrayList<>();
		surveys.add(survey);
		savedSurveys.postValue(surveys);
	}

	public void removeSurvey(Survey survey) {
		savedSurveys.getValue().remove(survey);
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
		availableSensors.getValue().stream().filter(DeviceSensor::isActive).forEach(deviceSensor -> {
			long deviceId = deviceRepo.saveDeviceSensor(deviceSensor);
			StudyDeviceSensorJoin join = new StudyDeviceSensorJoin(studyId, deviceId);
			studyDeviceSensorJoinRepo.saveStudyDeviceSensorJoin(join);
		});
	}

	private void saveSurveys(long studyId) {
		savedSurveys.getValue().forEach(survey -> survey.setStudyId(studyId));
		surveyRepo.saveSurveys(savedSurveys.getValue());
	}

	public List<Object> getSectionedDeviceSensors() {
		List<Object> sectionedDeviceSensors = new ArrayList<>();
		for (SensorGroup group : SensorGroup.values()) {
			List<DeviceSensor> sensorsOfGroup = availableSensors.getValue()
																.stream()
																.filter(availableDeviceSensor -> availableDeviceSensor.getType().getGroup().equals(group))
																.collect(Collectors.toList());
			sectionedDeviceSensors.add(group.getGroupId());
			sectionedDeviceSensors.addAll(sensorsOfGroup);
		}
		return sectionedDeviceSensors;
	}
}
