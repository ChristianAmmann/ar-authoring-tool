package ncxp.de.mobiledatacollection.viewmodel.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.model.repository.DataRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.model.repository.TestPersonRepository;
import ncxp.de.mobiledatacollection.viewmodel.StudiesViewModel;

public class StudiesViewModelFactory implements ViewModelProvider.Factory {

	private final StudyRepository                 studyRepository;
	private final StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository;
	private final TestPersonRepository            testPersonRepository;
	private final DataRepository                  dataRepository;
	private final SurveyRepository                surveyRepository;
	private final Application                     application;

	public StudiesViewModelFactory(Application application,
								   StudyRepository studyRepository,
								   StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository,
								   SurveyRepository surveyRepository,
								   TestPersonRepository testPersonRepository,
								   DataRepository dataRepository) {
		this.application = application;
		this.studyRepository = studyRepository;
		this.studyDeviceSensorJoinRepository = studyDeviceSensorJoinRepository;
		this.surveyRepository = surveyRepository;
		this.testPersonRepository = testPersonRepository;
		this.dataRepository = dataRepository;
	}

	@NonNull
	@Override
	public StudiesViewModel create(@NonNull Class modelClass) {
		return new StudiesViewModel(application, studyRepository, studyDeviceSensorJoinRepository, surveyRepository, testPersonRepository, dataRepository);
	}
}
