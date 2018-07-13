package ncxp.de.mobiledatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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



	/*private void exportDatabase() {
		if (!isExternalPermissionForDatabaseGranted()) {
			return;
		}
		try {
			String folderName = getString(R.string.app_name);
			File appFolder = new File(Environment.getExternalStorageDirectory() + File.separator + folderName);
			if (!appFolder.exists()) {
				appFolder.mkdirs();

			}
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String currentDateandTime = sdf.format(new Date());
			if (sd.canWrite()) {
				String currentDBPath = getDatabasePath(StudyDatabase.DATABASE_NAME).getAbsolutePath();
				String backupName = currentDateandTime + "_" + StudyDatabase.DATABASE_NAME;
				String backupDBPath = folderName + File.separator + backupName;
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
				showSnackbar("Datenbank unter " + folderName + File.separator + backupName + " exportiert");
			}
		} catch (Exception e) {
			showSnackbar("Leider ist ein Fehler aufgetreten!");
		}
	}*/


	private void showSnackbar(String text) {
		Snackbar snackbar = Snackbar.make(toolbar, text, Snackbar.LENGTH_INDEFINITE);
		snackbar.setAction(R.string.ok, (viewClicked) -> {
			snackbar.dismiss();
		});
		snackbar.addCallback(new Snackbar.Callback() {
			@Override
			public void onDismissed(Snackbar transientBottomBar, int event) {
				super.onDismissed(transientBottomBar, event);
				fab.setVisibility(View.VISIBLE);
				CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
				params.setAnchorId(R.id.bottom_app_bar);
				params.anchorGravity = Gravity.CENTER | Gravity.TOP;
			}
		});
		fab.setVisibility(View.GONE);
		snackbar.show();
	}

}
