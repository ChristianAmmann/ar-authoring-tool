package ncxp.de.mobiledatacollection.ui.study.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.StudyDeviceSensorJoin;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.repository.DeviceSensorRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;

public class StudyViewModel extends ViewModel {

	private StudyRepository                     studyRepo;
	private SurveyRepository                    surveyRepo;
	private DeviceSensorRepository              deviceRepo;
	private StudyDeviceSensorJoinRepository     studyDeviceSensorJoinRepo;
	private SensorDataManager                   sensorDataManager;
	private Study                               study;
	private MutableLiveData<List<Survey>>       currentCreatedSurveys;
	private MutableLiveData<List<DeviceSensor>> availableSensors;

	private LiveData<List<Survey>> savedSurveys;

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
		this.currentCreatedSurveys = new MutableLiveData<>();
		currentCreatedSurveys.setValue(new ArrayList<>());
		this.availableSensors = new MutableLiveData<>();
		availableSensors.setValue(new ArrayList<>());
	}


	public void init() {
		savedSurveys = surveyRepo.getSurveys();
		initAvailableDeviceSensor();
	}

	public LiveData<List<Survey>> getSavedSurveys() {
		return this.savedSurveys;
	}

	public MutableLiveData<List<DeviceSensor>> getAvailableSensors() {
		return availableSensors;
	}

	private void initAvailableDeviceSensor() {
		List<DeviceSensor> availableDeviceSensors = new ArrayList<>();
		sensorDataManager.getAvailableDeviceSensors().forEach(sensor -> {
			DeviceSensor deviceSensor = new DeviceSensor();
			deviceSensor.setSensor(sensor);
			if (deviceSensor.getType() != null) {
				availableDeviceSensors.add(deviceSensor);
			}
		});
		availableSensors.setValue(availableDeviceSensors);
	}

	public MutableLiveData<List<Survey>> getCurrentCreatedSurveys() {
		return this.currentCreatedSurveys;
	}

	public Survey createSurvey(String name, String description, String projectDirectory, String identifier) {
		Survey survey = new Survey();
		survey.setName(name);
		survey.setDescription(description);
		survey.setProjectDirectory(projectDirectory);
		survey.setIdentifier(identifier);
		currentCreatedSurveys.getValue().add(survey);
		return survey;
	}

	public void removeSurvey(Survey survey) {
		currentCreatedSurveys.getValue().remove(survey);
	}

	public void saveStudy(String name, String description) {
		Study study = new Study();
		study.setName(name);
		study.setDescription(description);
		long id = studyRepo.saveStudy(study);
		study.setId(id);
		availableSensors.getValue().stream().filter(DeviceSensor::isActive).forEach(deviceSensor -> {
			long deviceId = deviceRepo.saveDeviceSensor(deviceSensor);
			StudyDeviceSensorJoin join = new StudyDeviceSensorJoin(study.getId(), deviceId);
			studyDeviceSensorJoinRepo.saveStudyDeviceSensorJoin(join);
		});
	}
}
