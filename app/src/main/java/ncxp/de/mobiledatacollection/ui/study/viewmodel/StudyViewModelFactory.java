package ncxp.de.mobiledatacollection.ui.study.viewmodel;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.model.repository.DeviceSensorRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyMeasurementJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;

public class StudyViewModelFactory implements ViewModelProvider.Factory {

	private final StudyRepository                 studyRepository;
	private final SurveyRepository                surveyRepository;
	private final DeviceSensorRepository          deviceSensorRepository;
	private final SensorDataManager               sensorDataManager;
	private final StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository;
	private final StudyMeasurementJoinRepository  studyMeasurementJoinRepository;

	public StudyViewModelFactory(StudyRepository studyRepository,
								 SurveyRepository surveyRepository,
								 DeviceSensorRepository deviceSensorRepository,
								 SensorDataManager sensorDataManager,
								 StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository,
								 StudyMeasurementJoinRepository studyMeasurementJoinRepository) {
		this.studyRepository = studyRepository;
		this.surveyRepository = surveyRepository;
		this.deviceSensorRepository = deviceSensorRepository;
		this.sensorDataManager = sensorDataManager;
		this.studyDeviceSensorJoinRepository = studyDeviceSensorJoinRepository;
		this.studyMeasurementJoinRepository = studyMeasurementJoinRepository;
	}

	@NonNull
	@Override
	public StudyViewModel create(@NonNull Class modelClass) {
		return new StudyViewModel(studyRepository, surveyRepository, deviceSensorRepository, sensorDataManager, studyDeviceSensorJoinRepository, studyMeasurementJoinRepository);
	}
}