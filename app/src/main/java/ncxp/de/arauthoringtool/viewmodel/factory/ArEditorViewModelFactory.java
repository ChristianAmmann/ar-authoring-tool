package ncxp.de.arauthoringtool.viewmodel.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.arauthoringtool.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.repository.TestPersonRepository;
import ncxp.de.arauthoringtool.viewmodel.ArEditorViewModel;

public class ArEditorViewModelFactory implements ViewModelProvider.Factory {

	private final Application          application;
	private final ArSceneRepository    arSceneRepository;
	private final TestPersonRepository testPersonRepository;

	public ArEditorViewModelFactory(Application application, ArSceneRepository arSceneRepository, TestPersonRepository testPersonRepository) {
		this.application = application;
		this.arSceneRepository = arSceneRepository;
		this.testPersonRepository = testPersonRepository;
	}

	@NonNull
	@Override
	public ArEditorViewModel create(@NonNull Class modelClass) {
		return new ArEditorViewModel(application, arSceneRepository, testPersonRepository);
	}
}
