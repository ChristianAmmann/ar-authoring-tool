package ncxp.de.mobiledatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.ui.arscene.ArSceneFragment;
import ncxp.de.mobiledatacollection.ui.arscene.viewmodel.ArSceneViewModel;
import ncxp.de.mobiledatacollection.ui.arscene.viewmodel.ArSceneViewModelFactory;

public class ArSceneActivity extends AppCompatActivity {

	private FloatingActionButton fab;
	private Toolbar              toolbar;

	public static ArSceneViewModel obtainViewModel(FragmentActivity activity) {
		return ViewModelProviders.of(activity, createFactory(activity)).get(ArSceneViewModel.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arscene);

		fab = findViewById(R.id.fab_add_arscene);
		fab.setOnClickListener(view -> {
			Intent intent = new Intent(this, ArImageMarkerActivity.class);
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
