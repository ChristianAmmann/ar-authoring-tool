package ncxp.de.arauthoringtool.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;

public class ArSceneViewModel extends ViewModel {

	private ArSceneRepository              arSceneRepo;
	private Study                          study;
	private MutableLiveData<List<ARScene>> arScenes;

	public ArSceneViewModel(ArSceneRepository arSceneRepo) {
		this.arSceneRepo = arSceneRepo;
		this.arScenes = new MutableLiveData<>();
	}

	public void init() {
		loadArScenes();
	}

	private void loadArScenes() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<ARScene> fetchedData = arSceneRepo.getArScenes();
			arScenes.postValue(fetchedData);
		});
	}

	public LiveData<List<ARScene>> geArScenes() {
		return this.arScenes;
	}

	public void deleteArScene(ARScene arScene) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			arSceneRepo.deleteArScene(arScene);
			List<ARScene> currentScenes = arScenes.getValue();
			currentScenes.remove(arScene);
			arScenes.postValue(currentScenes);
		});
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}
}
