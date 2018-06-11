package ncxp.de.mobiledatacollection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ncxp.de.mobiledatacollection.ui.studies.StudiesFragment;

public class StudiesActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studies_activity);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.container, StudiesFragment.newInstance()).commitNow();
		}
	}
}
