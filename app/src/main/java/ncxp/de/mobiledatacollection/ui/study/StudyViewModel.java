package ncxp.de.mobiledatacollection.ui.study;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.datalogger.sensor.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;

public class StudyViewModel extends ViewModel {

	private StudyRepository   studyRepo;
	private SurveyRepository  surveyRepo;
	private SensorDataManager sensorDataManager;

	private LiveData<List<Survey>> surveys;

	public StudyViewModel(StudyRepository studyRepo, SurveyRepository surveyRepo, SensorDataManager sensorDataManager) {
		this.studyRepo = studyRepo;
		this.surveyRepo = surveyRepo;
		this.sensorDataManager = sensorDataManager;
	}


	public void init() {
		surveys = surveyRepo.getSurveys();
	}

	public LiveData<List<Survey>> getSurveys() {
		return this.surveys;
	}

	public List<DeviceSensor> getAvailableDeviceSensor() {
		return sensorDataManager.getAvailableDeviceSensors();
	}

}
