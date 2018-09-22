package ncxp.de.arauthoringtool.viewmodel.factory;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.viewmodel.MappingViewModel;

public class MappingViewModelFactory implements ViewModelProvider.Factory {

	private ArSceneRepository arSceneRepository;

	public MappingViewModelFactory(ArSceneRepository arSceneRepository) {
		this.arSceneRepository = arSceneRepository;
	}

	@NonNull
	@Override
	public MappingViewModel create(@NonNull Class modelClass) {
		return new MappingViewModel(arSceneRepository);
	}
}
