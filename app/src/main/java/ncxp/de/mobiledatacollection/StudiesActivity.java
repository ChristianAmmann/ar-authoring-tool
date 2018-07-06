package ncxp.de.mobiledatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.ui.studies.StudiesFragment;
import ncxp.de.mobiledatacollection.ui.studies.viewmodel.StudiesViewModel;
import ncxp.de.mobiledatacollection.ui.studies.viewmodel.StudiesViewModelFactory;

public class StudiesActivity extends AppCompatActivity {


	private FloatingActionButton fab;

	public static StudiesViewModel obtainViewModel(FragmentActivity activity) {
		return ViewModelProviders.of(activity, createFactory(activity)).get(StudiesViewModel.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_studies);
		fab = findViewById(R.id.fab_add_study);
		fab.setOnClickListener(view -> {
			Intent intent = new Intent(this, StudyActivity.class);
			startActivity(intent);
		});
		Toolbar toolbar = findViewById(R.id.action_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationOnClickListener(this::showArActivity);
		showFragment(savedInstanceState);
	}

	private void showArActivity(View view) {
		Intent intent = new Intent(this, ArActivity.class);
		startActivity(intent);
	}

	private void showFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, StudiesFragment.newInstance(), null).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}


	private static StudiesViewModelFactory createFactory(FragmentActivity activity) {
		StudyDatabase database = StudyDatabase.getInstance(activity);
		StudyDao studyDao = database.study();
		StudyRepository studyRepo = new StudyRepository(studyDao);
		return new StudiesViewModelFactory(studyRepo);
	}
}
