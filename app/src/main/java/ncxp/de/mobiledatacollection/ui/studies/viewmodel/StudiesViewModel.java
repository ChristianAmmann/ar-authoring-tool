package ncxp.de.mobiledatacollection.ui.studies.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;

public class StudiesViewModel extends ViewModel {

	private MediatorLiveData<List<Study>>   studies;
	private StudyRepository                 studyRepo;
	private StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository;

	public StudiesViewModel(StudyRepository studyRepo, StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository) {
		this.studyRepo = studyRepo;
		this.studyDeviceSensorJoinRepository = studyDeviceSensorJoinRepository;
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
					myStudies.forEach(this::loadDevicesSensors);
					studies.setValue(myStudies);
				}
			});
		});
	}

	private void loadDevicesSensors(Study study) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<DeviceSensor> deviceSensorsForStudy = studyDeviceSensorJoinRepository.getDeviceSensorsForStudy(study);
			int position = studies.getValue().indexOf(study);
			study.setSensors(deviceSensorsForStudy);
			studies.getValue().remove(position);
			studies.getValue().add(position, study);
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
