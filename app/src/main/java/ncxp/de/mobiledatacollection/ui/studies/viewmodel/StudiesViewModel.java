package ncxp.de.mobiledatacollection.ui.studies.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;

public class StudiesViewModel extends ViewModel {

	private MediatorLiveData<List<Study>> studies;
	private StudyRepository               studyRepo;

	public StudiesViewModel(StudyRepository studyRepo) {
		this.studyRepo = studyRepo;
		studies = new MediatorLiveData<>();
	}

	public LiveData<List<Study>> getStudies() {
		return this.studies;
	}

	public void init() {
		loadStudies();
	}

	private void loadStudies() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			LiveData<List<Study>> fetchedData = studyRepo.getStudies();
			studies.addSource(fetchedData, (myStudies) -> {
				if (myStudies != null) {
					studies.removeSource(fetchedData);
					studies.setValue(myStudies);
				}
			});
		});
	}


	public void deleteStudy(Study study) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			studyRepo.deleteStudy(study);
		});
	}

	public void refreshStudies() {
		loadStudies();
	}
}
