package ncxp.de.arauthoringtool.viewmodel.factory;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.arauthoringtool.sensorlogger.SensorDataManager;
import ncxp.de.arauthoringtool.model.repository.DeviceSensorRepository;
import ncxp.de.arauthoringtool.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.arauthoringtool.model.repository.StudyRepository;
import ncxp.de.arauthoringtool.model.repository.SurveyRepository;
import ncxp.de.arauthoringtool.viewmodel.StudyViewModel;

public class StudyViewModelFactory implements ViewModelProvider.Factory {

	private final StudyRepository                 studyRepository;
	private final SurveyRepository                surveyRepository;
	private final DeviceSensorRepository          deviceSensorRepository;
	private final SensorDataManager               sensorDataManager;
	private final StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository;

	public StudyViewModelFactory(StudyRepository studyRepository,
								 SurveyRepository surveyRepository,
								 DeviceSensorRepository deviceSensorRepository,
								 SensorDataManager sensorDataManager,
								 StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository) {
		this.studyRepository = studyRepository;
		this.surveyRepository = surveyRepository;
		this.deviceSensorRepository = deviceSensorRepository;
		this.sensorDataManager = sensorDataManager;
		this.studyDeviceSensorJoinRepository = studyDeviceSensorJoinRepository;
	}

	@NonNull
	@Override
	public StudyViewModel create(@NonNull Class modelClass) {
		return new StudyViewModel(studyRepository, surveyRepository, deviceSensorRepository, sensorDataManager, studyDeviceSensorJoinRepository);
	}
}