package ncxp.de.mobiledatacollection;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ncxp.de.mobiledatacollection.sceneform.AugmentedImageAnchor;
import ncxp.de.mobiledatacollection.sceneform.CursorNode;
import ncxp.de.mobiledatacollection.sceneform.ObjectARImageNode;
import ncxp.de.mobiledatacollection.sceneform.PlaceholderNode;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModel;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModelFactory;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ThumbnailAdapter;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ThumbnailListener;

public class ArImageMarkerActivity extends AppCompatActivity implements ThumbnailListener {

	private static final String TAG                    = ArImageMarkerActivity.class.getCanonicalName();
	private static final int    CAMERA_PERMISSION_CODE = 1234;
	private static final String FILE_TYPE              = ".sfb";

	private ModelRenderable        frameRenderable;
	private ImageView              fitToScanView;
	private ImageButton            expandThumbnailButton;
	private ImageButton            saveSceneButton;
	private boolean                installRequested;
	private Session                session;
	private boolean                shouldConfigureSession = false;
	private ArSceneView            arSceneView;
	private RecyclerView           modelRecyclerView;
	private ThumbnailAdapter       thumbnailAdapter;
	private Map<String, Node>      augmentedImageMap      = new HashMap<>();
	private ArImageMarkerViewModel viewModel;
	private Node                   currentSelection;
	private CursorNode             cursorNode;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arSceneView = findViewById(R.id.surfaceview);
		fitToScanView = findViewById(R.id.image_view_fit_to_scan);
		modelRecyclerView = findViewById(R.id.model_thumbnail_list);
		expandThumbnailButton = findViewById(R.id.expand_thumbnail_button);
		saveSceneButton = findViewById(R.id.save_scene);
		saveSceneButton.setOnClickListener(view -> {
			viewModel.save(augmentedImageMap);
			//TODO finish Activity
		});
		obtainViewModel();
		installRequested = false;
		initializeSceneView();
		initPlacing();
		setupAdapter();
		viewModel.getThumbnails().observe(this, drawables -> thumbnailAdapter.replaceItems(drawables));
		viewModel.init();
		initBottomBar();
	}

	private void setupAdapter() {
		modelRecyclerView.setHasFixedSize(true);
		thumbnailAdapter = new ThumbnailAdapter(new ArrayList<>(), this);
		modelRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		modelRecyclerView.setAdapter(thumbnailAdapter);
	}

	private void initBottomBar() {
		expandThumbnailButton.setOnClickListener((view) -> {
			int visibility = modelRecyclerView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
			CoordinatorLayout.LayoutParams paramsExpand = (CoordinatorLayout.LayoutParams) expandThumbnailButton.getLayoutParams();
			if (visibility == View.GONE) {
				paramsExpand.setAnchorId(R.id.surfaceview);
				paramsExpand.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
				expandThumbnailButton.setImageResource(R.drawable.chevron_up);
			} else {
				paramsExpand.setAnchorId(R.id.model_thumbnail_list);
				paramsExpand.anchorGravity = Gravity.TOP | Gravity.CENTER;
				expandThumbnailButton.setImageResource(R.drawable.chevron_down);
			}
			modelRecyclerView.setVisibility(visibility);
		});
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
				fitToScanView.setVisibility(View.GONE);
				if (!augmentedImageMap.containsKey(augmentedImage.getName())) {
					AugmentedImageAnchor node = new AugmentedImageAnchor();
					node.setImage(augmentedImage);
					PlaceholderNode placeholder = new PlaceholderNode(frameRenderable);
					placeholder.setParent(node);
					placeholder.setOnTapListener((hitTestResult, motionEvent) -> {
						showBottomThumbnails();
						currentSelection = placeholder;
						cursorNode.setParent(placeholder);
					});
					arSceneView.getScene().addChild(node);
					augmentedImageMap.put(augmentedImage.getName(), node);
				} else {
					//TODO maybe nothing
				}
			}
		}
	}

	private void initPlacing() {
		ModelRenderable.builder().setSource(this, R.raw.frame).build().thenAccept(renderable -> frameRenderable = renderable);
		ModelRenderable.builder().setSource(this, R.raw.arrow).build().thenAccept(renderable -> cursorNode = new CursorNode(renderable));
	}

	private void showBottomThumbnails() {
		expandThumbnailButton.setVisibility(View.VISIBLE);
		modelRecyclerView.setVisibility(View.VISIBLE);
		CoordinatorLayout.LayoutParams paramsExpand = (CoordinatorLayout.LayoutParams) expandThumbnailButton.getLayoutParams();
		paramsExpand.setAnchorId(R.id.model_thumbnail_list);
		paramsExpand.anchorGravity = Gravity.TOP | Gravity.CENTER;
		expandThumbnailButton.setImageResource(R.drawable.chevron_down);
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

	private void obtainViewModel() {
		viewModel = ViewModelProviders.of(this, createFactory()).get(ArImageMarkerViewModel.class);
	}

	private ArImageMarkerViewModelFactory createFactory() {
		return new ArImageMarkerViewModelFactory(getApplication());
	}

	@Override
	public void onThumbnailClicked(String imageName) {
		if (currentSelection == null) {
			return;
		}
		String sjbFile = imageName.replace("png", "sfb");
		ModelRenderable.builder().setSource(this, Uri.parse(sjbFile)).build().thenAccept(renderable -> {
			AugmentedImageAnchor parent = (AugmentedImageAnchor) currentSelection.getParent();
			parent.removeChild(currentSelection);
			augmentedImageMap.remove(parent.getName());
			ObjectARImageNode node = new ObjectARImageNode(renderable);
			node.setParent(parent);
			node.setOnTapListener((hitTestResult, motionEvent) -> {
				currentSelection = node;
				cursorNode.setParent(node);
			});
			augmentedImageMap.put(parent.getName(), node);
			showSaveButton();
			currentSelection = node;
			cursorNode.setParent(node);
		}).exceptionally(throwable -> {
			Toast toast = Toast.makeText(this, "Laden der SFB Datei nicht möglich. Heißen Bilddatei und 3D Modell-Datei gleich?", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return null;
		});
	}

	private void showSaveButton() {
		saveSceneButton.setVisibility(View.VISIBLE);
	}
}
