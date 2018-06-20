package ncxp.de.mobiledatacollection;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ncxp.de.mobiledatacollection.ui.study.OthersFragment;
import ncxp.de.mobiledatacollection.ui.study.SensorFragment;
import ncxp.de.mobiledatacollection.ui.study.SurveyFragment;

public class StudyActivity extends AppCompatActivity {

	private Toolbar              toolbar;
	private BottomNavigationView navigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity);
		toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.new_study);
		navigationView = findViewById(R.id.bottom_navigation);
		navigationView.setOnNavigationItemSelectedListener(this::onNavigationItemClicked);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationOnClickListener(view -> this.finish());
		showSensorFragment(savedInstanceState);
	}


	private boolean onNavigationItemClicked(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sensors:
				getSupportFragmentManager().beginTransaction().replace(R.id.container, SensorFragment.newInstance(), null).commit();
				break;
			case R.id.survey:
				getSupportFragmentManager().beginTransaction().replace(R.id.container, SurveyFragment.newInstance(), null).commit();
				break;
			case R.id.others:
				getSupportFragmentManager().beginTransaction().replace(R.id.container, OthersFragment.newInstance(), null).commit();
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

			boolean canDismissDialog = true;
			if (titleInput.getText().toString().isEmpty()) {
				titleInput.setError("Die Studie benötigt einen Titel");
				canDismissDialog = false;
			}

			if (descriptionInput.getText().toString().isEmpty()) {
				descriptionInput.setError("Die Studie benötigt eine Beschreibung");
				canDismissDialog = false;
			}
			if (canDismissDialog) {
				alertDialog.dismiss();
			}

		});
	}
}
