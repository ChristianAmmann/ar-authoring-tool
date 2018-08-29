package ncxp.de.mobiledatacollection.viewmodel.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.viewmodel.ArEditorViewModel;

public class ArEditorViewModelFactory implements ViewModelProvider.Factory {

	private final Application       application;
	private final ArSceneRepository arSceneRepository;

	public ArEditorViewModelFactory(Application application, ArSceneRepository arSceneRepository) {
		this.application = application;
		this.arSceneRepository = arSceneRepository;
	}

	@NonNull
	@Override
	public ArEditorViewModel create(@NonNull Class modelClass) {
		return new ArEditorViewModel(application, arSceneRepository);
	}
}
