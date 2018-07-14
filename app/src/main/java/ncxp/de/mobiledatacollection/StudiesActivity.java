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
import android.view.MenuItem;
import android.view.View;

import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.repository.DataRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.model.repository.TestPersonRepository;
import ncxp.de.mobiledatacollection.ui.studies.StudiesFragment;
import ncxp.de.mobiledatacollection.ui.studies.viewmodel.StudiesViewModel;
import ncxp.de.mobiledatacollection.ui.studies.viewmodel.StudiesViewModelFactory;

public class StudiesActivity extends AppCompatActivity {

	private static final int EXTERNAL_PERMISSION_CODE = 4001;

	private FloatingActionButton fab;
	private Toolbar              toolbar;

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
		toolbar = findViewById(R.id.action_bar);
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
		StudyRepository studyRepo = new StudyRepository(database.study());
		StudyDeviceSensorJoinRepository studyDeviceRepo = new StudyDeviceSensorJoinRepository(database.studyDeviceSensorJoinDao());
		TestPersonRepository testPersonRepo = new TestPersonRepository(database.testPerson());
		DataRepository dataRepo = new DataRepository(database.dataDao());
		SurveyRepository surveyRepository = new SurveyRepository(database.survey());
		return new StudiesViewModelFactory(activity.getApplication(), studyRepo, studyDeviceRepo, surveyRepository, testPersonRepo, dataRepo);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.help:
				break;
			case R.id.impressum:
				break;
		}
		return true;
	}
}
