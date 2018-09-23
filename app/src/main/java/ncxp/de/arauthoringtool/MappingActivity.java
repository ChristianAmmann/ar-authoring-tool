package ncxp.de.arauthoringtool;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import ncxp.de.arauthoringtool.model.StudyDatabase;
import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.ui.mapping.MappingFragment;
import ncxp.de.arauthoringtool.viewmodel.MappingViewModel;
import ncxp.de.arauthoringtool.viewmodel.factory.MappingViewModelFactory;

public class MappingActivity extends AppCompatActivity {

	public static final String               ARSCENE_KEY = "arscene_key";
	public static final String               KEY_STUDY   = "study_key";
	private             FloatingActionButton fab;

	public static MappingViewModel obtainViewModel(FragmentActivity activity) {
		ARScene arScene = activity.getIntent().getParcelableExtra(ARSCENE_KEY);
		Study study = activity.getIntent().getParcelableExtra(KEY_STUDY);
		MappingViewModel viewModel = ViewModelProviders.of(activity, createFactory(activity)).get(MappingViewModel.class);
		viewModel.setArScene(arScene);
		viewModel.setStudy(study);
		return viewModel;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapping);
		fab = findViewById(R.id.fab_play);
		fab.setOnClickListener(view -> {
			//TODO start ArEditor
		});
		showFragment(savedInstanceState);
	}

	private void showFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.mapping_container, MappingFragment.newInstance(), null).commit();
		}
	}

	private static MappingViewModelFactory createFactory(FragmentActivity activity) {
		StudyDatabase database = StudyDatabase.getInstance(activity);
		ArSceneRepository arSceneRepository = new ArSceneRepository(database.arSceneDao(), database.arImageToObjectRelationDao());
		return new MappingViewModelFactory(activity.getApplication(), arSceneRepository);
	}
}
