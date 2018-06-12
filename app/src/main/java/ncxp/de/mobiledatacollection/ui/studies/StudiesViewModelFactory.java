package ncxp.de.mobiledatacollection.ui.studies;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
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
