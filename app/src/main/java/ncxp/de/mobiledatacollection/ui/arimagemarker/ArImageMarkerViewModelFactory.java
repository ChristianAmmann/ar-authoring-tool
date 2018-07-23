package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ArImageMarkerViewModelFactory implements ViewModelProvider.Factory {

	private final Application application;

	public ArImageMarkerViewModelFactory(Application application) {
		this.application = application;
	}

	@NonNull
	@Override
	public ArImageMarkerViewModel create(@NonNull Class modelClass) {
		return new ArImageMarkerViewModel(application);
	}
}
