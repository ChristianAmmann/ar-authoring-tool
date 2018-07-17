package ncxp.de.mobiledatacollection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ncxp.de.mobiledatacollection.sceneform.AugmentedImageNode;

public class ArImageMarkerActivity extends AppCompatActivity {

	private static final String TAG                    = ArImageMarkerActivity.class.getCanonicalName();
	private static final int    CAMERA_PERMISSION_CODE = 1234;

	private       ImageView                                  fitToScanView;
	private       boolean                                    installRequested;
	private       Session                                    session;
	private       boolean                                    shouldConfigureSession = false;
	// Rendering. The Renderers are created here, and initialized when the GL surface is created.
	private       ArSceneView                                arSceneView;
	private final Map<Integer, Pair<AugmentedImage, Anchor>> augmentedImageMap      = new HashMap<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arSceneView = findViewById(R.id.surfaceview);
		fitToScanView = findViewById(R.id.image_view_fit_to_scan);

		installRequested = false;
		initializeSceneView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (session == null) {
			Exception exception = null;
			String message = null;
			try {
				switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
					case INSTALL_REQUESTED:
						installRequested = true;
						return;
					case INSTALLED:
						break;
				}

				// ARCore requires camera permissions to operate. If we did not yet obtain runtime
				// permission on Android M and above, now is a good time to ask the user for it.
				if (!hasCameraPermission()) {
					requestCameraPermission();
					return;
				}

				session = new Session(/* context = */ this);
			} catch (UnavailableArcoreNotInstalledException | UnavailableUserDeclinedInstallationException e) {
				message = "Please install ARCore";
				exception = e;
			} catch (UnavailableApkTooOldException e) {
				message = "Please update ARCore";
				exception = e;
			} catch (UnavailableSdkTooOldException e) {
				message = "Please update this app";
				exception = e;
			} catch (Exception e) {
				message = "This device does not support AR";
				exception = e;
			}

			if (message != null) {
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
				Log.e(TAG, "Exception creating session", exception);
				return;
			}

			shouldConfigureSession = true;
		}

		if (shouldConfigureSession) {
			configureSession();
			shouldConfigureSession = false;
			arSceneView.setupSession(session);
		}

		// Note that order matters - see the note in onPause(), the reverse applies here.
		try {
			session.resume();
			arSceneView.resume();
		} catch (CameraNotAvailableException e) {
			// In some cases (such as another camera app launching) the camera may be given to
			// a different app instead. Handle this properly by showing a message and recreate the
			// session at the next iteration.
			Toast.makeText(this, "Camera not available. Please restart the app.", Toast.LENGTH_LONG).show();
			session = null;
			return;
		}
	}

	private void initializeSceneView() {
		arSceneView.getScene().setOnUpdateListener((this::onUpdateFrame));
	}

	private void onUpdateFrame(FrameTime frameTime) {
		Frame frame = arSceneView.getArFrame();
		Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

		for (AugmentedImage augmentedImage : updatedAugmentedImages) {
			if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
				// Check camera image matches our reference image
				if (augmentedImage.getName().equals("delorean")) {
					AugmentedImageNode node = new AugmentedImageNode(this, "model.sfb");
					node.setImage(augmentedImage);
					arSceneView.getScene().addChild(node);
				}

			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (session != null) {
			// Note that the order matters - GLSurfaceView is paused first so that it does not try
			// to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
			// still call session.update() and get a SessionPausedException.
			arSceneView.pause();
			session.pause();
		}
	}

	private void configureSession() {
		Config config = new Config(session);
		if (!setupAugmentedImageDatabase(config)) {
			Toast.makeText(this, "Could not setup augmented image database", Toast.LENGTH_LONG).show();
		}
		session.configure(config);
	}

	private boolean setupAugmentedImageDatabase(Config config) {
		AugmentedImageDatabase augmentedImageDatabase;

		try (InputStream is = getAssets().open("markers.imgdb")) {
			augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
		} catch (IOException e) {
			Log.e(TAG, "IO exception loading augmented image database.", e);
			return false;
		}
		config.setAugmentedImageDatabase(augmentedImageDatabase);
		return true;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			this.getWindow()
				.getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
						.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	private boolean shouldShowRequestPermissionRationale() {
		return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
	}


	private boolean hasCameraPermission() {
		return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

	private void requestCameraPermission() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
		if (hasCameraPermission()) {
			Toast.makeText(this, "Camera permissions are needed to run this application", Toast.LENGTH_LONG).show();
			if (!shouldShowRequestPermissionRationale()) {
				// Permission denied with checking "Do not ask again".
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.fromParts("package", this.getPackageName(), null));
				startActivity(intent);
			}
			finish();
		}
	}
}
