package ncxp.de.mobiledatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.ui.areditor.util.EditorState;
import ncxp.de.mobiledatacollection.ui.arscene.ArSceneFragment;
import ncxp.de.mobiledatacollection.viewmodel.ArSceneViewModel;
import ncxp.de.mobiledatacollection.viewmodel.factory.ArSceneViewModelFactory;

public class ArSceneActivity extends AppCompatActivity {

	public static final String KEY_STUDY = "key_study";

	private FloatingActionButton fab;
	private Toolbar              toolbar;
	private ArSceneViewModel     viewModel;

	public static ArSceneViewModel obtainViewModel(FragmentActivity activity) {
		Study study = activity.getIntent().getParcelableExtra(KEY_STUDY);
		ArSceneViewModel viewModel = ViewModelProviders.of(activity, createFactory(activity)).get(ArSceneViewModel.class);
		viewModel.setStudy(study);
		return viewModel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arscene);
		viewModel = ArSceneActivity.obtainViewModel(this);
		fab = findViewById(R.id.fab_add_arscene);
		fab.setOnClickListener(view -> {
			Intent intent = new Intent(this, ArEditorActivity.class);
			intent.putExtra(ArEditorActivity.KEY_STUDY, viewModel.getStudy());
			intent.putExtra(ArEditorActivity.KEY_EDITOR_STATE, EditorState.EDIT_MODE);
			startActivity(intent);
		});
		toolbar = findViewById(R.id.arscene_toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(view -> this.finish());
		showFragment(savedInstanceState);
	}

	private void showFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, ArSceneFragment.newInstance(), null).commit();
		}
	}

	private static ArSceneViewModelFactory createFactory(FragmentActivity activity) {
		StudyDatabase database = StudyDatabase.getInstance(activity);
		ArSceneRepository arSceneRepository = new ArSceneRepository(database.arSceneDao(), database.arImageToObjectRelationDao());
		return new ArSceneViewModelFactory(arSceneRepository);
	}
}
