package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;

public class ArImageMarkerViewModelFactory implements ViewModelProvider.Factory {

	private final Application       application;
	private final ArSceneRepository arSceneRepository;

	public ArImageMarkerViewModelFactory(Application application, ArSceneRepository arSceneRepository) {
		this.application = application;
		this.arSceneRepository = arSceneRepository;
	}

	@NonNull
	@Override
	public ArImageMarkerViewModel create(@NonNull Class modelClass) {
		return new ArImageMarkerViewModel(application, arSceneRepository);
	}
}
