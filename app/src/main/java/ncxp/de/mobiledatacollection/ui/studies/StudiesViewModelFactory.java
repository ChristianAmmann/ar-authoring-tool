package ncxp.de.mobiledatacollection.ui.studies;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.model.repository.StudyRepository;

public class StudiesViewModelFactory implements ViewModelProvider.Factory {

	private final StudyRepository studyRepository;

	public StudiesViewModelFactory(StudyRepository studyRepository) {
		this.studyRepository = studyRepository;
	}

	@NonNull
	@Override
	public StudiesViewModel create(@NonNull Class modelClass) {
		return new StudiesViewModel(studyRepository);
	}
}
