package ncxp.de.mobiledatacollection.ui.studies;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;

public class StudiesViewModel extends ViewModel {

	private LiveData<List<Study>> studies;
	private StudyRepository       studyRepo;

	public StudiesViewModel(StudyRepository studyRepo) {
		this.studyRepo = studyRepo;
	}


	public void init() {
		if (this.studies != null) {
			return;
		}
		studies = studyRepo.getStudies();
	}

	public LiveData<List<Study>> getStudies() {
		return this.studies;
	}

	public void deleteStudy(Study study) {
		studyRepo.deleteStudy(study);
	}
}
