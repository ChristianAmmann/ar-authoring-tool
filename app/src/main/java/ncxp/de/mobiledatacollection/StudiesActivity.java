package ncxp.de.mobiledatacollection;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import ncxp.de.mobiledatacollection.ui.studies.StudiesFragment;

public class StudiesActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studies_activity);
		this.showFragment(savedInstanceState);
	}

	private void showFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, StudiesFragment.newInstance(), null).commit();
		}
	}
}
