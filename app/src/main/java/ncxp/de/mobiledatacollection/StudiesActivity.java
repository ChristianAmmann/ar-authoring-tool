package ncxp.de.mobiledatacollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import ncxp.de.mobiledatacollection.ui.studies.StudiesFragment;

public class StudiesActivity extends AppCompatActivity {


	private FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studies_activity);
		fab = findViewById(R.id.fab);
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
}
