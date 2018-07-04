package ncxp.de.mobiledatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.DeviceSensorDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDeviceSensorJoinDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.repository.DeviceSensorRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.ui.study.OthersFragment;
import ncxp.de.mobiledatacollection.ui.study.SensorFragment;
import ncxp.de.mobiledatacollection.ui.study.SurveyFragment;
import ncxp.de.mobiledatacollection.ui.study.adapter.ViewPagerAdapter;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.StudyViewModel;
import ncxp.de.mobiledatacollection.ui.study.viewmodel.StudyViewModelFactory;

public class StudyActivity extends AppCompatActivity {

	private Toolbar              toolbar;
	private BottomNavigationView navigationView;
	private ViewPager            viewPager;
	private MenuItem             previousMenuItem;
	private StudyViewModel       viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study);
		toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.new_study);
		viewPager = findViewById(R.id.container);
		setupViewPagerAdapter();
		navigationView = findViewById(R.id.bottom_navigation);
		navigationView.setOnNavigationItemSelectedListener(this::onNavigationItemClicked);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationOnClickListener(view -> this.finish());
		configureViewModel();
	}

	public static StudyViewModel obtainViewModel(FragmentActivity activity) {
		return ViewModelProviders.of(activity, createFactory(activity)).get(StudyViewModel.class);
	}

	private void configureViewModel() {
		StudyViewModelFactory factory = createFactory(this);
		viewModel = ViewModelProviders.of(this, factory).get(StudyViewModel.class);
		viewModel.init();
	}

	private static StudyViewModelFactory createFactory(FragmentActivity activity) {
		StudyDatabase database = StudyDatabase.getInstance(activity);
		StudyDao studyDao = database.study();
		SurveyDao surveyDao = database.survey();
		DeviceSensorDao deviceDao = database.deviceSensor();
		StudyDeviceSensorJoinDao studyDeviceSensorJoinDao = database.studyDeviceSensorJoinDao();
		StudyRepository studyRepo = new StudyRepository(studyDao);
		SurveyRepository surveyRepo = new SurveyRepository(surveyDao);
		DeviceSensorRepository deviceRepo = new DeviceSensorRepository(deviceDao);
		SensorDataManager sensorDataManager = SensorDataManager.getInstance(activity);
		StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepo = new StudyDeviceSensorJoinRepository(studyDeviceSensorJoinDao);
		return new StudyViewModelFactory(studyRepo, surveyRepo, deviceRepo, sensorDataManager, studyDeviceSensorJoinRepo);
	}

	private void setupViewPagerAdapter() {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(SensorFragment.newInstance());
		adapter.addFragment(SurveyFragment.newInstance());
		adapter.addFragment(OthersFragment.newInstance());
		viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

			@Override
			public void onPageSelected(int position) {
				if (previousMenuItem != null) {
					previousMenuItem.setChecked(false);
				} else {
					navigationView.getMenu().getItem(0).setChecked(false);
				}
				navigationView.getMenu().getItem(position).setChecked(true);
				previousMenuItem = navigationView.getMenu().getItem(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}


	private boolean onNavigationItemClicked(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.sensors:
				viewPager.setCurrentItem(0);
				break;
			case R.id.survey:
				viewPager.setCurrentItem(1);
				break;
			case R.id.others:
				viewPager.setCurrentItem(2);
				break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.study_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				showSaveDialog();
				break;
		}
		return true;
	}

	private void showSaveDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		builder.setTitle(R.string.dialog_study_save_title);
		View dialogView = inflater.inflate(R.layout.dialog_study_save, null);
		TextInputEditText titleInput = dialogView.findViewById(R.id.study_title);
		TextInputEditText descriptionInput = dialogView.findViewById(R.id.study_description);
		builder.setView(dialogView).setPositiveButton(R.string.save, null);

		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		Button positiveButtion = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		positiveButtion.setOnClickListener((view) -> {
			boolean validInput = true;
			validInput &= validateInput(titleInput, R.string.dialog_study_error_title);
			validInput &= validateInput(descriptionInput, R.string.dialog_study_error_description);

			if (validInput) {
				String name = titleInput.getText().toString();
				String description = descriptionInput.getText().toString();
				viewModel.saveStudy(name, description);
				alertDialog.dismiss();
				finish();
			}

		});
	}

	private boolean validateInput(TextInputEditText editText, int errorCode) {
		if (editText.getText().toString().isEmpty()) {
			editText.setError(getString(errorCode));
			return false;
		}
		return true;
	}
}
