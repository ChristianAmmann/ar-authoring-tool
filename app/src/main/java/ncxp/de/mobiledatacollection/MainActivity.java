package ncxp.de.mobiledatacollection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	public static final String ONBOARDING_KEY       = "onboarding_on_start";
	public static final String ONBOARDING_COMPLETED = "onboarding_completed";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initApp();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initApp();
	}

	private void initApp() {
		if (!isCompleted()) {
			Intent intent = new Intent(this, OnboardingAppActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, StudiesActivity.class);
			startActivity(intent);
		}
		finish();
	}


	private boolean isCompleted() {
		SharedPreferences sharedPreferences = getSharedPreferences(ONBOARDING_KEY, MODE_PRIVATE);
		return sharedPreferences.getBoolean(ONBOARDING_COMPLETED, false);
	}
}