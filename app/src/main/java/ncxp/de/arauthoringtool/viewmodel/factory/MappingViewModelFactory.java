package ncxp.de.arauthoringtool.viewmodel.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.viewmodel.MappingViewModel;

public class MappingViewModelFactory implements ViewModelProvider.Factory {

	private final ArSceneRepository arSceneRepository;
	private       Application       application;

	public MappingViewModelFactory(@NonNull Application application, ArSceneRepository arSceneRepository) {
		this.application = application;
		this.arSceneRepository = arSceneRepository;
	}

	@NonNull
	@Override
	public MappingViewModel create(@NonNull Class modelClass) {
		return new MappingViewModel(application, arSceneRepository);
	}
}
