package ncxp.de.arauthoringtool;

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

import ncxp.de.arauthoringtool.model.StudyDatabase;
import ncxp.de.arauthoringtool.model.repository.DataRepository;
import ncxp.de.arauthoringtool.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.arauthoringtool.model.repository.StudyRepository;
import ncxp.de.arauthoringtool.model.repository.SurveyRepository;
import ncxp.de.arauthoringtool.model.repository.TestPersonRepository;
import ncxp.de.arauthoringtool.ui.studies.StudiesFragment;
import ncxp.de.arauthoringtool.viewmodel.StudiesViewModel;
import ncxp.de.arauthoringtool.viewmodel.factory.StudiesViewModelFactory;

public class StudiesActivity extends AppCompatActivity {

	public static StudiesViewModel obtainViewModel(FragmentActivity activity) {
		return ViewModelProviders.of(activity, createFactory(activity)).get(StudiesViewModel.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_studies);

		FloatingActionButton fab = findViewById(R.id.fab_add_study);
		fab.setOnClickListener(view -> {
			Intent intent = new Intent(this, StudyActivity.class);
			startActivity(intent);
		});
		Toolbar toolbar = findViewById(R.id.action_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		showFragment(savedInstanceState);
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
				Intent intentOnboarding = new Intent(this, OnboardingAppActivity.class);
				startActivity(intentOnboarding);
				finish();
				break;
			case R.id.impressum:
				Intent intentAbout = new Intent(this, AboutActivity.class);
				startActivity(intentAbout);
				break;
		}
		return true;
	}
}
