package ncxp.de.mobiledatacollection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ncxp.de.mobiledatacollection.rendering.BackgroundRenderer;
import ncxp.de.mobiledatacollection.util.DisplayRotationHelper;

public class ArImageMarkerActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

	private static final String TAG                    = ArImageMarkerActivity.class.getCanonicalName();
	private static final int    CAMERA_PERMISSION_CODE = 1234;

	private       GLSurfaceView                              surfaceView;
	private       ImageView                                  fitToScanView;
	private       boolean                                    installRequested;
	private       Session                                    session;
	private       boolean                                    shouldConfigureSession = false;
	private       DisplayRotationHelper                      displayRotationHelper;
	private final BackgroundRenderer                         backgroundRenderer     = new BackgroundRenderer();
	//private final AugmentedImageRenderer                     augmentedImageRenderer = new AugmentedImageRenderer();
	// Augmented image and its associated center pose anchor, keyed by index of the augmented image in
	// the
	// database.
	private final Map<Integer, Pair<AugmentedImage, Anchor>> augmentedImageMap      = new HashMap<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);

		displayRotationHelper = new DisplayRotationHelper(this);

		surfaceView = findViewById(R.id.surfaceview);
		fitToScanView = findViewById(R.id.image_view_fit_to_scan);
		surfaceView.setPreserveEGLContextOnPause(true);
		surfaceView.setEGLContextClientVersion(2);
		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		surfaceView.setRenderer(this);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		installRequested = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (session == null) {
			initSession();
		}
		if (shouldConfigureSession) {
			configureSession();
			shouldConfigureSession = false;
		}
		if (session == null) {
			return;
		}
		// Note that order matters - see the note in onPause(), the reverse applies here.
		try {
			session.resume();
		} catch (CameraNotAvailableException e) {
			// In some cases (such as another camera app launching) the camera may be given to
			// a different app instead. Handle this properly by showing a message and recreate the
			// session at the next iteration.
			Toast.makeText(this, "Camera not available. Please restart the app.", Toast.LENGTH_LONG).show();
			session = null;
			return;
		}
		surfaceView.onResume();
		displayRotationHelper.onResume();
	}

	private void initSession() {
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

			if (!hasCameraPermission()) {
				requestCameraPermission();
				return;
			}
			session = new Session(this);
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

	@Override
	public void onPause() {
		super.onPause();
		if (session != null) {
			// Note that the order matters - GLSurfaceView is paused first so that it does not try
			// to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
			// still call session.update() and get a SessionPausedException.
			surfaceView.onPause();
			displayRotationHelper.onPause();

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
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		try {
			// Create the texture and pass it to ARCore session to be filled during update().
			backgroundRenderer.createOnGlThread(/*context=*/ this);
			//augmentedImageRenderer.createOnGlThread(/*context=*/ this);
		} catch (IOException e) {
			Log.e(TAG, "Failed to read an asset file", e);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		displayRotationHelper.onSurfaceChanged(width, height);
		GLES20.glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		if (session == null) {
			return;
		}

		displayRotationHelper.updateSessionIfNeeded(session);

		try {
			session.setCameraTextureName(backgroundRenderer.getTextureId());

			// Obtain the current frame from ARSession. When the configuration is set to
			// UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
			// camera framerate.
			Frame frame = session.update();
			Camera camera = frame.getCamera();

			// Draw background.
			backgroundRenderer.draw(frame);

			// If not tracking, don't draw 3d objects.
			if (camera.getTrackingState() == TrackingState.PAUSED) {
				return;
			}

			// Get projection matrix.
			float[] projmtx = new float[16];
			camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

			// Get camera matrix and draw.
			float[] viewmtx = new float[16];
			camera.getViewMatrix(viewmtx, 0);

			// Compute lighting from average intensity of the image.
			final float[] colorCorrectionRgba = new float[4];
			frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

			// Visualize augmented images.
			//TODO drawAugmentedImages(frame, projmtx, viewmtx, colorCorrectionRgba);
		} catch (Throwable t) {
			// Avoid crashing the application due to unhandled exceptions.
			Log.e(TAG, "Exception on the OpenGL thread", t);
		}
	}

	private void drawAugmentedImages(Frame frame, float[] projmtx, float[] viewmtx, float[] colorCorrectionRgba) {
		Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

		// Iterate to update augmentedImageMap, remove elements we cannot draw.
		for (AugmentedImage augmentedImage : updatedAugmentedImages) {
			switch (augmentedImage.getTrackingState()) {
				case PAUSED:
					// When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
					// but not yet tracked.
					String text = String.format("Detected Image %d", augmentedImage.getIndex());
					Toast.makeText(this, text, Toast.LENGTH_LONG).show();
					break;

				case TRACKING:
					// Have to switch to UI Thread to update View.
					this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							fitToScanView.setVisibility(View.GONE);
						}
					});

					// Create a new anchor for newly found images.
					if (!augmentedImageMap.containsKey(augmentedImage.getIndex())) {
						Anchor centerPoseAnchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
						augmentedImageMap.put(augmentedImage.getIndex(), Pair.create(augmentedImage, centerPoseAnchor));
					}
					break;

				case STOPPED:
					augmentedImageMap.remove(augmentedImage.getIndex());
					break;

				default:
					break;
			}
		}

		// Draw all images in augmentedImageMap
		for (Pair<AugmentedImage, Anchor> pair : augmentedImageMap.values()) {
			AugmentedImage augmentedImage = pair.first;
			Anchor centerAnchor = augmentedImageMap.get(augmentedImage.getIndex()).second;
			switch (augmentedImage.getTrackingState()) {
				case TRACKING:
					//TODO augmentedImageRenderer.draw(viewmtx, projmtx, augmentedImage, centerAnchor, colorCorrectionRgba);
					break;
				default:
					break;
			}
		}
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
