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
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.ArImageToObjectRelationDao;
import ncxp.de.mobiledatacollection.model.dao.ArSceneDao;
import ncxp.de.mobiledatacollection.model.data.ARScene;
import ncxp.de.mobiledatacollection.model.data.ArImageToObjectRelation;
import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.sceneform.AugmentedImageAnchor;
import ncxp.de.mobiledatacollection.sceneform.CursorNode;
import ncxp.de.mobiledatacollection.sceneform.DeleteNode;
import ncxp.de.mobiledatacollection.sceneform.ObjectARImageNode;
import ncxp.de.mobiledatacollection.sceneform.PlaceholderNode;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModel;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModelFactory;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ThumbnailAdapter;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ThumbnailListener;

public class ArImageMarkerActivity extends AppCompatActivity implements ThumbnailListener {

	private static final String TAG                    = ArImageMarkerActivity.class.getCanonicalName();
	private static final int    CAMERA_PERMISSION_CODE = 1234;
	public static final  String ARSCENE_KEY            = "arscene_key";
	private static final String FILE_TYPE              = ".sfb";

	private ModelRenderable        frameRenderable;
	private ImageView              fitToScanView;
	private ImageButton            expandThumbnailButton;
	private ImageButton            saveSceneButton;
	private ImageButton            backSceneButton;
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
	private DeleteNode             deleteNode;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arSceneView = findViewById(R.id.surfaceview);
		fitToScanView = findViewById(R.id.image_view_fit_to_scan);
		modelRecyclerView = findViewById(R.id.model_thumbnail_list);
		expandThumbnailButton = findViewById(R.id.expand_thumbnail_button);
		saveSceneButton = findViewById(R.id.save_scene);
		saveSceneButton.setOnClickListener(view -> showSaveDialog());
		backSceneButton = findViewById(R.id.back_arscene);
		backSceneButton.setOnClickListener(view -> finish());
		arSceneView.getPlaneRenderer().setVisible(false);

		obtainViewModel();
		installRequested = false;
		initializeSceneView();
		initPlacing();
		setupAdapter();
		viewModel.getThumbnails().observe(this, drawables -> thumbnailAdapter.replaceItems(drawables));
		viewModel.init();
		if (viewModel.getArScene() != null) {
			setupAugmentedImageMap();
		}
		initBottomBar();
	}

	private void setupAugmentedImageMap() {

	}

	private void setupAdapter() {
		modelRecyclerView.setHasFixedSize(true);
		thumbnailAdapter = new ThumbnailAdapter(new ArrayList<>(), this);
		modelRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		modelRecyclerView.setAdapter(thumbnailAdapter);
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
				updateAugmentedImage(augmentedImage);
			}
		}
	}

	private void updateAugmentedImage(AugmentedImage augmentedImage) {
		if (!augmentedImageMap.containsKey(augmentedImage.getName())) {
			//TODO ViewModel ARScene Edit. Check if for augmentedImage is a entry
			AugmentedImageAnchor imageAnchor = new AugmentedImageAnchor();
			imageAnchor.setImage(augmentedImage);
			if (viewModel.containsArSceneObject(augmentedImage.getName())) {
				ArImageToObjectRelation arSceneObjectFileName = viewModel.getArSceneObjectFileName(augmentedImage.getName());
				createObjectARImageNode(imageAnchor, arSceneObjectFileName.getImageName());
				showExpandThumbnailButton();
			} else {
				attachNewPlaceholder(imageAnchor);
				augmentedImageMap.put(augmentedImage.getName(), imageAnchor);
			}
			arSceneView.getScene().addChild(imageAnchor);
		}
	}

	private PlaceholderNode attachNewPlaceholder(Node parent) {
		PlaceholderNode placeholder = new PlaceholderNode(frameRenderable);
		placeholder.setOnTapListener((hitTestResult, motionEvent) -> {
			showBottomThumbnails();
			attachCursorNode(placeholder);
		});
		placeholder.setParent(parent);
		return placeholder;
	}

	private void initPlacing() {
		ModelRenderable.builder().setSource(this, R.raw.frame).build().thenAccept(renderable -> frameRenderable = renderable);
		ModelRenderable.builder().setSource(this, R.raw.arrow).build().thenAccept(renderable -> cursorNode = new CursorNode(renderable));
		ViewRenderable.builder().setView(this, R.layout.delete_view).build().thenAccept((renderable) -> deleteNode = new DeleteNode(renderable));
	}

	private void showBottomThumbnails() {
		expandThumbnailButton.setVisibility(View.VISIBLE);
		modelRecyclerView.setVisibility(View.VISIBLE);
		CoordinatorLayout.LayoutParams paramsExpand = (CoordinatorLayout.LayoutParams) expandThumbnailButton.getLayoutParams();
		paramsExpand.setAnchorId(R.id.model_thumbnail_list);
		paramsExpand.anchorGravity = Gravity.TOP | Gravity.CENTER;
		expandThumbnailButton.setImageResource(R.drawable.chevron_down);
	}

	private void showExpandThumbnailButton() {
		expandThumbnailButton.setVisibility(View.VISIBLE);
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
		ARScene arScene = getIntent().getParcelableExtra(ARSCENE_KEY);
		viewModel.setArScene(arScene);
	}

	private ArImageMarkerViewModelFactory createFactory() {
		StudyDatabase database = StudyDatabase.getInstance(this);
		ArSceneDao arSceneDao = database.arSceneDao();
		ArImageToObjectRelationDao arImageToObjectRelationDao = database.arImageToObjectRelationDao();
		ArSceneRepository arSceneRepository = new ArSceneRepository(arSceneDao, arImageToObjectRelationDao);
		return new ArImageMarkerViewModelFactory(getApplication(), arSceneRepository);
	}

	@Override
	public void onThumbnailClicked(String imageName) {
		if (currentSelection == null) {
			return;
		}
		replaceObjectAR(imageName, currentSelection);
	}

	private void createObjectARImageNode(Node parent, String imageName) {
		String sjbFile = imageName.replace("png", "sfb");
		ModelRenderable.builder().setSource(this, Uri.parse(sjbFile)).build().thenAccept(renderable -> {
			ObjectARImageNode node = new ObjectARImageNode(sjbFile, renderable);
			node.setParent(parent);
			node.setOnTapListener((hitTestResult, motionEvent) -> {
				attachCursorNode(node);
				attachDeleteNode(node);
			});
			if (parent instanceof AugmentedImageAnchor) {
				AugmentedImageAnchor augmentedImageAnchor = (AugmentedImageAnchor) parent;
				augmentedImageMap.put(augmentedImageAnchor.getImage().getName(), node);
			}
			attachCursorNode(node);
			showSaveButton();
		}).exceptionally(throwable -> {
			Toast toast = Toast.makeText(this, "Laden der SFB Datei nicht möglich. Heißen Bilddatei und 3D Modell-Datei gleich?", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return null;
		});
	}

	private void replaceObjectAR(String imageName, Node parent) {
		Node anchor = parent;
		if (parent != null) {
			anchor = parent.getParent();
			anchor.removeChild(parent);
		}
		createObjectARImageNode(anchor, imageName);
	}


	private void attachDeleteNode(Node node) {
		deleteNode.setParent(node);
		deleteNode.setOnTapListener((hitTestResult, motionEvent) -> {
			AugmentedImageAnchor imageAnchor = (AugmentedImageAnchor) node.getParent();
			imageAnchor.removeChild(node);
			attachNewPlaceholder(imageAnchor);
			showSaveButton();
			attachCursorNode(null);
		});
	}

	private void attachCursorNode(Node node) {
		if (currentSelection == null || !currentSelection.equals(node)) {
			currentSelection = node;
			cursorNode.setParent(node);
			attachDeleteNode(null);
		}

	}

	private void showSaveButton() {
		if (augmentedImageMap.values().stream().anyMatch(node -> node instanceof ObjectARImageNode)) {
			saveSceneButton.setVisibility(View.VISIBLE);
		} else {
			saveSceneButton.setVisibility(View.GONE);
		}
	}

	private void showSaveDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		builder.setTitle(R.string.dialog_title_save_ar_scene);
		View dialogView = inflater.inflate(R.layout.dialog_save_arscene, null);
		TextInputEditText titleInput = dialogView.findViewById(R.id.arscene_title_edit);
		TextInputEditText descriptionInput = dialogView.findViewById(R.id.arscene_description_edit);
		builder.setView(dialogView).setPositiveButton(R.string.save, (dialog, which) -> {
			boolean validInput = validateInput(titleInput, R.string.dialog_arscene_error_title);
			validInput &= validateInput(descriptionInput, R.string.dialog_arscene_error_description);
			if (validInput) {
				String title = titleInput.getText().toString();
				String description = descriptionInput.getText().toString();
				if (viewModel.getArScene() != null) {
					ARScene arScene = viewModel.getArScene();
					arScene.setName(title);
					arScene.setDescription(description);
					viewModel.update(arScene, augmentedImageMap);
				} else {
					viewModel.save(title, description, augmentedImageMap);
				}
				finish();
			}

		});
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
		builder.setCancelable(false);
		if (viewModel.getArScene() != null) {
			titleInput.setText(viewModel.getArScene().getName());
			descriptionInput.setText(viewModel.getArScene().getDescription());
		}
		builder.create().show();
	}


	private boolean validateInput(TextInputEditText editText, int errorCode) {
		if (editText.getText().toString().isEmpty()) {
			editText.setError(getString(errorCode));
			return false;
		}
		return true;
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
}
