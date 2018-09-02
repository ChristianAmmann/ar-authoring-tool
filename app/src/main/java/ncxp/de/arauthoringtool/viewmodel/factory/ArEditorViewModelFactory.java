package ncxp.de.arauthoringtool.viewmodel.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.viewmodel.ArEditorViewModel;

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
