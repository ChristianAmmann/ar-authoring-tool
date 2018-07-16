package ncxp.de.mobiledatacollection.ui.arscene.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ncxp.de.mobiledatacollection.model.data.ARScene;
import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;

public class ArSceneViewModel extends ViewModel {

	private ArSceneRepository arSceneRepo;

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
			//TODO get Mapping
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
}
