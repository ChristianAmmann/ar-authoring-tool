package ncxp.de.arauthoringtool.view.imprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ncxp.de.arauthoringtool.R;

public class AboutActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		Toolbar toolbar = findViewById(R.id.about_toolbar);
		toolbar.setNavigationOnClickListener(view -> finish());
	}
}
