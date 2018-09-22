package ncxp.de.arauthoringtool;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import ncxp.de.arauthoringtool.model.StudyDatabase;
import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.ui.mapping.MappingFragment;
import ncxp.de.arauthoringtool.viewmodel.MappingViewModel;
import ncxp.de.arauthoringtool.viewmodel.factory.MappingViewModelFactory;

public class MappingActivity extends AppCompatActivity {

	private FloatingActionButton fab;

	public static MappingViewModel obtainViewModel(FragmentActivity activity) {
		MappingViewModel viewModel = ViewModelProviders.of(activity, createFactory(activity)).get(MappingViewModel.class);
		return viewModel;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapping);
		fab = findViewById(R.id.fab_play);
		fab.setOnClickListener(view -> {

		});
		showFragment(savedInstanceState);
	}

	private void showFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.mapping_container, MappingFragment.newInstance(), null);
		}
	}

	private static MappingViewModelFactory createFactory(FragmentActivity activity) {
		StudyDatabase database = StudyDatabase.getInstance(activity);
		ArSceneRepository arSceneRepository = new ArSceneRepository(database.arSceneDao(), database.arImageToObjectRelationDao());
		return new MappingViewModelFactory(arSceneRepository);
	}
}
