package ncxp.de.mobiledatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
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

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

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
	public static final  String AUGMENTED_IMAGE_DB     = "markers.imgdb";

	private ArFragment             arFragment;
	private Session                session;
	private ModelRenderable        frameRenderable;
	private ImageView              fitToScanView;
	private ImageButton            expandThumbnailButton;
	private ImageButton            saveSceneButton;
	private ImageButton            backSceneButton;
	private RecyclerView           modelRecyclerView;
	private ThumbnailAdapter       thumbnailAdapter;
	private Map<String, Node>      augmentedImageMap = new HashMap<>();
	private ArImageMarkerViewModel viewModel;
	private Node                   currentSelection;
	private CursorNode             cursorNode;
	private DeleteNode             deleteNode;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_marker_fragment);
		fitToScanView = findViewById(R.id.image_view_fit_to_scan);
		modelRecyclerView = findViewById(R.id.model_thumbnail_list);
		expandThumbnailButton = findViewById(R.id.expand_thumbnail_button);
		saveSceneButton = findViewById(R.id.save_scene);
		saveSceneButton.setOnClickListener(view -> showSaveDialog());
		backSceneButton = findViewById(R.id.back_arscene);
		backSceneButton.setOnClickListener(view -> finish());
		arFragment.getArSceneView().getPlaneRenderer().setVisible(false);
		arFragment.getArSceneView().getScene().setOnUpdateListener(this::onUpdateFrame);
		arFragment.getPlaneDiscoveryController().hide();
		arFragment.getPlaneDiscoveryController().setInstructionView(null);
		obtainViewModel();
		initPlacing();
		setupAdapter();
		viewModel.getThumbnails().observe(this, drawables -> thumbnailAdapter.replaceItems(drawables));
		viewModel.init();
		initBottomBar();
		setupAugmentedImageDatabase();
	}

	private void setupAdapter() {
		modelRecyclerView.setHasFixedSize(true);
		thumbnailAdapter = new ThumbnailAdapter(new ArrayList<>(), this);
		modelRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		modelRecyclerView.setAdapter(thumbnailAdapter);
	}

	private void onUpdateFrame(FrameTime frameTime) {
		Frame frame = arFragment.getArSceneView().getArFrame();
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
			arFragment.getArSceneView().getScene().addChild(imageAnchor);
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

	private boolean setupAugmentedImageDatabase() {
		session = arFragment.getArSceneView().getSession();
		if (session == null) {
			try {
				arFragment.getArSceneView().setupSession(new Session(this));
			} catch (UnavailableArcoreNotInstalledException | UnavailableSdkTooOldException | UnavailableApkTooOldException e) {
				e.printStackTrace();
			}
			session = arFragment.getArSceneView().getSession();
		}
		AugmentedImageDatabase augmentedImageDatabase;
		try (InputStream is = getAssets().open(AUGMENTED_IMAGE_DB)) {
			augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
		} catch (IOException e) {
			Log.e(TAG, "IO exception loading augmented image database.", e);
			return false;
		}
		Config config = new Config(session);
		config.setAugmentedImageDatabase(augmentedImageDatabase);
		session.configure(config);
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
				paramsExpand.setAnchorId(R.id.ar_marker_fragment);
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
