package ncxp.de.arauthoringtool.viewmodel;

import android.arch.lifecycle.ViewModel;

import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;

public class MappingViewModel extends ViewModel {

	private ArSceneRepository arSceneRepository;

	public MappingViewModel(ArSceneRepository arSceneRepository) {
		this.arSceneRepository = arSceneRepository;
	}


}
