package ncxp.de.arauthoringtool.viewmodel.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import ncxp.de.arauthoringtool.viewmodel.MappingViewModel;

public class MappingViewModelFactory implements ViewModelProvider.Factory {

	private Application application;

	public MappingViewModelFactory(@NonNull Application application) {
		this.application = application;
	}

	@NonNull
	@Override
	public MappingViewModel create(@NonNull Class modelClass) {
		return new MappingViewModel(application);
	}
}
