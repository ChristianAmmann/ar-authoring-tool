package ncxp.de.mobiledatacollection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class ArCrosshairsTestActivity extends AppCompatActivity {
	private static final String TAG = ArCrosshairsTestActivity.class.getSimpleName();

	private ArFragment      arFragment;
	private ModelRenderable andyRenderable;
	private GestureDetector trackableGestureDetector;

	@Override
	@SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
	// CompletableFuture requires api level 24
	// FutureReturnValueIgnored is not valid
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_ar_crosshair);
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
	}


}

