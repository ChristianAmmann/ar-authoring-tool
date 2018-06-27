package ncxp.de.mobiledatacollection.ui.study;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.mobiledatacollection.datalogger.AvailableDeviceSensor;
import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;

public class StudyViewModel extends ViewModel {

	private StudyRepository                              studyRepo;
	private SurveyRepository                             surveyRepo;
	private SensorDataManager                            sensorDataManager;
	private Study                                        study;
	private MutableLiveData<List<Survey>>                currentCreatedSurveys;
	private MutableLiveData<List<AvailableDeviceSensor>> availableSensors;

	private LiveData<List<Survey>> savedSurveys;

	public StudyViewModel(StudyRepository studyRepo, SurveyRepository surveyRepo, SensorDataManager sensorDataManager) {
		this.studyRepo = studyRepo;
		this.surveyRepo = surveyRepo;
		this.sensorDataManager = sensorDataManager;
		this.currentCreatedSurveys = new MutableLiveData<>();
		currentCreatedSurveys.setValue(new ArrayList<>());
		this.availableSensors = new MutableLiveData<>();
		availableSensors.setValue(new ArrayList<>());
	}


	public void init() {
		savedSurveys = surveyRepo.getSurveys();
	}

	public LiveData<List<Survey>> getSavedSurveys() {
		return this.savedSurveys;
	}

	public MutableLiveData<List<AvailableDeviceSensor>> getAvailableDeviceSensor() {
		List<AvailableDeviceSensor> availableDeviceSensors = new ArrayList<>();
		sensorDataManager.getAvailableDeviceSensors().forEach(sensor -> {
			AvailableDeviceSensor availableDeviceSensor = new AvailableDeviceSensor(sensor);
			if (availableDeviceSensor.getType() != null) {
				availableDeviceSensors.add(availableDeviceSensor);
			}

		});
		availableSensors.setValue(availableDeviceSensors);
		return availableSensors;
	}

	public MutableLiveData<List<Survey>> getCurrentCreatedSurveys() {
		return this.currentCreatedSurveys;
	}

	public Survey createSurvey(String name, String description, String platformId) {
		Survey survey = new Survey();
		survey.setName(name);
		survey.setDescription(description);
		survey.setPlatformId(platformId);
		currentCreatedSurveys.getValue().add(survey);
		return survey;
	}

	public void removeSurvey(Survey survey) {
		currentCreatedSurveys.getValue().remove(survey);
	}
}
