package ncxp.de.mobiledatacollection.ui.studies.viewmodel;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;

public class StudiesViewModelFactory implements ViewModelProvider.Factory {

	private final StudyRepository                 studyRepository;
	private final StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository;

	public StudiesViewModelFactory(StudyRepository studyRepository, StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository) {
		this.studyRepository = studyRepository;
		this.studyDeviceSensorJoinRepository = studyDeviceSensorJoinRepository;
	}

	@NonNull
	@Override
	public StudiesViewModel create(@NonNull Class modelClass) {
		return new StudiesViewModel(studyRepository, studyDeviceSensorJoinRepository);
	}
}
