package ncxp.de.mobiledatacollection.viewmodel.factory;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.viewmodel.ArSceneViewModel;

public class ArSceneViewModelFactory implements ViewModelProvider.Factory {

	private ArSceneRepository arSceneRepository;

	public ArSceneViewModelFactory(ArSceneRepository arSceneRepository) {
		this.arSceneRepository = arSceneRepository;
	}

	@NonNull
	@Override
	public ArSceneViewModel create(@NonNull Class modelClass) {
		return new ArSceneViewModel(arSceneRepository);
	}
}
