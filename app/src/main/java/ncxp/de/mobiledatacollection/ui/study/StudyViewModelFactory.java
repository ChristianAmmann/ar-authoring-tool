package ncxp.de.mobiledatacollection.ui.study;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;

public class StudyViewModelFactory implements ViewModelProvider.Factory {

	private final StudyRepository   studyRepository;
	private final SurveyRepository  surveyRepository;
	private final SensorDataManager sensorDataManager;

	public StudyViewModelFactory(StudyRepository studyRepository, SurveyRepository surveyRepository, SensorDataManager sensorDataManager) {
		this.studyRepository = studyRepository;
		this.surveyRepository = surveyRepository;
		this.sensorDataManager = sensorDataManager;
	}

	@NonNull
	@Override
	public StudyViewModel create(@NonNull Class modelClass) {
		return new StudyViewModel(studyRepository, surveyRepository, sensorDataManager);
	}
}