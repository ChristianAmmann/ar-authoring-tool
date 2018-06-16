package ncxp.de.mobiledatacollection;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ncxp.de.mobiledatacollection.ui.study.OthersFragment;
import ncxp.de.mobiledatacollection.ui.study.SensorFragment;
import ncxp.de.mobiledatacollection.ui.study.SurveyFragment;

public class StudyActivity extends AppCompatActivity {

	private Toolbar              toolbar;
	private BottomNavigationView navigationView;
	private FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity);
		toolbar = findViewById(R.id.toolbar);
		toolbar.setNavigationOnClickListener(view -> this.finish());
		toolbar.setTitle(R.string.new_study);
		fab = findViewById(R.id.fab);
		navigationView = findViewById(R.id.bottom_navigation);
		navigationView.setOnNavigationItemSelectedListener(this::onNavigationItemClicked);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		showSensorFragment(savedInstanceState);
	}


	private boolean onNavigationItemClicked(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sensors:
				getSupportFragmentManager().beginTransaction().replace(R.id.container, SensorFragment.newInstance(), null).commit();
				fab.setVisibility(View.INVISIBLE);
				break;
			case R.id.survey:
				getSupportFragmentManager().beginTransaction().replace(R.id.container, SurveyFragment.newInstance(), null).commit();
				fab.setVisibility(View.VISIBLE);
				break;
			case R.id.others:
				getSupportFragmentManager().beginTransaction().replace(R.id.container, OthersFragment.newInstance(), null).commit();
				fab.setVisibility(View.INVISIBLE);
		}
		return true;
	}

	private void showSensorFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, SensorFragment.newInstance(), null).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.study_menu, menu);
		return true;
	}
}
