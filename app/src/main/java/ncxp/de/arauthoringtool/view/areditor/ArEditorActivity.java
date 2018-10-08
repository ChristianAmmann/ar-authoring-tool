package ncxp.de.arauthoringtool.view.areditor;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.StudyDatabase;
import ncxp.de.arauthoringtool.model.dao.ArObjectDao;
import ncxp.de.arauthoringtool.model.dao.ArSceneDao;
import ncxp.de.arauthoringtool.model.dao.TestPersonDao;
import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.ArObject;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.repository.TestPersonRepository;
import ncxp.de.arauthoringtool.sceneform.ArNode;
import ncxp.de.arauthoringtool.sceneform.DeleteWidgetNode;
import ncxp.de.arauthoringtool.sceneform.ImageAnchor;
import ncxp.de.arauthoringtool.sceneform.RotateWidgetNode;
import ncxp.de.arauthoringtool.sceneform.ScaleWidgetNode;
import ncxp.de.arauthoringtool.view.areditor.util.EditorState;
import ncxp.de.arauthoringtool.view.areditor.util.RotationTechnique;
import ncxp.de.arauthoringtool.view.areditor.util.ScaleTechnique;
import ncxp.de.arauthoringtool.viewmodel.ArEditorViewModel;
import ncxp.de.arauthoringtool.viewmodel.factory.ArEditorViewModelFactory;

public class ArEditorActivity extends AppCompatActivity implements ArInteractionListener {

	private static final String TAG                = ArEditorActivity.class.getCanonicalName();
	public static final  String ARSCENE_KEY        = "arscene_key";
	public static final  String KEY_STUDY          = "study_key";
	public static final  String KEY_EDITOR_STATE   = "editor_state_key";
	private static final String FILE_TYPE_SFB      = "sfb";
	private static final String FILE_TYPE_PNG      = "png";
	public static final  String AUGMENTED_IMAGE_DB = "markers.imgdb";
	private static final float  MAX_SCALE          = 4f;
	private static final float  MIN_SCALE          = 0.1f;
	private static final float  SENSITIVITY_SCALE  = 0.4f;

	private ArFragment        arFragment;
	private ArEditFragment    editFragment;
	private ArStudyFragment   studyFragment;
	private ArEditorViewModel viewModel;
	private DeleteWidgetNode  deleteWidgetNode;
	private ScaleWidgetNode   scaleWidgetNode;
	private RotateWidgetNode  rotateWidgetNode;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_marker_fragment);
		viewModel = obtainViewModel();
		initDeleteWidgetNode();
		initRotateWidgetNode();
		initScaleWidgetNode();
		setupAugmentedImageDatabase();
		setupSelectionTechnique();
		setupRotationTechnique();
		setupScaleTechnique();
		viewModel.resetInteractionTechnique();
		setupArFragment();
		if (viewModel.getEditorState().equals(EditorState.EDIT_MODE)) {
			showEditModeFragment();
		} else {
			showStudyModeFragment();
		}
	}

	private void setupSelectionTechnique() {
		viewModel.getSelectionTechnique().observe(this, technique -> {
			arFragment.getTransformationSystem().getSelectionVisualizer().removeSelectionVisual(arFragment.getTransformationSystem().getSelectedNode());
			switch (technique) {
				case RAYCASTING:
					onRaycastingTechnique();
					break;
				case NONE:
					onNoneSelectionTechnique();
					break;
			}
		});
	}

	private void onRaycastingTechnique() {
		arFragment.setOnTapArPlaneListener(this::onTapArPlane);
		viewModel.getArNodes().stream().forEach(arNode -> arNode.setOnTapListener((hitTestResult, motionEvent) -> onArNodeTapped(arNode)));
	}

	private void onNoneSelectionTechnique() {
		arFragment.setOnTapArPlaneListener(null);
		viewModel.getArNodes().stream().forEach(arNode -> arNode.setOnTapListener(null));
	}

	private void setupRotationTechnique() {
		viewModel.getRotationTechnique().observe(this, technique -> {
			switch (technique) {
				case TWO_FINGER:
					onTwoFingerTechnique();
					break;
				case WIDGET_3D:
				case NONE:
					onNoneRotationTechnique();
					break;
			}
			removeRotationWidget();
		});
	}

	private void onTwoFingerTechnique() {
		viewModel.getArNodes().forEach(node -> node.getRotationController().setEnabled(true));
	}

	private void onNoneRotationTechnique() {
		viewModel.getArNodes().forEach(node -> node.getRotationController().setEnabled(false));
	}

	private void setupScaleTechnique() {
		viewModel.getScaleTechnique().observe(this, technique -> {
			switch (technique) {
				case PINCH:
					viewModel.getArNodes().forEach(node -> node.getScaleController().setEnabled(true));
					break;
				case WIDGET_3D:
				case NONE:
					viewModel.getArNodes().forEach(node -> node.getScaleController().setEnabled(false));
					break;
			}
			removeScaleWidget();
		});
	}

	private void removeScaleWidget() {
		if (scaleWidgetNode != null && scaleWidgetNode.getParent() != null) {
			Node parent = scaleWidgetNode.getParent();
			parent.removeChild(scaleWidgetNode);
		}
	}

	private void removeRotationWidget() {
		if (rotateWidgetNode != null && rotateWidgetNode.getParent() != null) {
			Node parent = rotateWidgetNode.getParent();
			parent.removeChild(rotateWidgetNode);
		}
	}

	private void removeDeleteWidget() {
		if (deleteWidgetNode != null && deleteWidgetNode.getParent() != null) {
			Node parent = deleteWidgetNode.getParent();
			parent.removeChild(deleteWidgetNode);
		}
	}

	private void showEditModeFragment() {
		if (editFragment == null) {
			editFragment = ArEditFragment.newInstance();
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.editor_container, editFragment, null).commit();
	}

	private void showStudyModeFragment() {
		if (studyFragment == null) {
			studyFragment = ArStudyFragment.newInstance();
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.editor_container, studyFragment, null).commit();
	}

	private void setupArFragment() {
		arFragment.getArSceneView().getScene().setOnTouchListener((hitTestResult, motionEvent) -> {
			if (hitTestResult.getNode() == null) {
				removeDeleteWidget();
			}
			return false;
		});
		arFragment.setOnTapArPlaneListener(this::onTapArPlane);
		arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
	}

	private void onTapArPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
		viewModel.setCurrentSelectedNode(null);
		removeDeleteWidget();

		if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
			return;
		}
		if (viewModel.getCurrentImageSelection() == null) {
			return;
		}
		if (viewModel.getEditorState().equals(EditorState.EDIT_MODE)) {
			Anchor anchor = hitResult.createAnchor();
			AnchorNode anchorNode = new AnchorNode(anchor);
			anchorNode.setParent(arFragment.getArSceneView().getScene());
			createARObject(anchorNode, viewModel.getCurrentImageSelection(), Quaternion.identity(), Vector3.one());
		}
	}

	private void onUpdateFrame(FrameTime frameTime) {
		Frame frame = arFragment.getArSceneView().getArFrame();
		Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
		for (AugmentedImage augmentedImage : updatedAugmentedImages) {
			if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
				updateAugmentedImage(augmentedImage);
			}
		}
	}

	private void updateAugmentedImage(AugmentedImage augmentedImage) {
		if (viewModel.containsAugmentedImage(augmentedImage) && !viewModel.isAugmentedImageUsed(augmentedImage)) {
			viewModel.getAugmentedImagesUsed().add(augmentedImage.getName());
			ImageAnchor imageAnchor = new ImageAnchor();
			imageAnchor.setImage(augmentedImage);
			ArObject arObject = viewModel.getArObject(augmentedImage);
			createARObject(imageAnchor, arObject.getImageName(), arObject.getRotation(), arObject.getScale());
			arFragment.getArSceneView().getScene().addChild(imageAnchor);
		}
	}

	private void initDeleteWidgetNode() {
		ViewRenderable.builder().setView(this, R.layout.delete_view).build().thenAccept((renderable) -> deleteWidgetNode = new DeleteWidgetNode(renderable));
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initRotateWidgetNode() {
		ViewRenderable.builder().setView(this, R.layout.rotation_widget_controls).build().thenAccept(viewRenderable -> {
			rotateWidgetNode = new RotateWidgetNode(viewRenderable);
			View widgetView = viewRenderable.getView();
			ImageButton rotateRightButton = widgetView.findViewById(R.id.rotate_right);
			ImageButton rotateLeftButton = widgetView.findViewById(R.id.rotate_left);
			rotateRightButton.setOnTouchListener((view, motionEvent) -> rotateNode(viewModel.getCurrentSelectedNode().getValue(), 2f));
			rotateLeftButton.setOnTouchListener((view, motionEvent) -> rotateNode(viewModel.getCurrentSelectedNode().getValue(), -2f));
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initScaleWidgetNode() {
		ViewRenderable.builder().setView(this, R.layout.scale_widget_controls).build().thenAccept(viewRenderable -> {
			scaleWidgetNode = new ScaleWidgetNode(viewRenderable);
			View widgetView = viewRenderable.getView();
			ImageButton scaleUpButton = widgetView.findViewById(R.id.scale_up);
			ImageButton scaleDownButton = widgetView.findViewById(R.id.scale_down);
			scaleUpButton.setOnTouchListener((view, motionEvent) -> scaleNode(viewModel.getCurrentSelectedNode().getValue(), 1.05f));
			scaleDownButton.setOnTouchListener((view, motionEvent) -> scaleNode(viewModel.getCurrentSelectedNode().getValue(), 0.95f));

		});
	}

	private boolean rotateNode(Node node, float degrees) {
		if (node != null) {
			Node parent = node.getParent();
			parent.removeChild(node);
			Vector3 axis = new Vector3(0, 1, 0);
			Quaternion currentRotation = node.getWorldRotation();
			Quaternion addRotation = Quaternion.axisAngle(axis, degrees);
			Quaternion newRotation = Quaternion.multiply(currentRotation, addRotation);
			node.setWorldRotation(newRotation);
			node.setParent(parent);
			return true;
		}
		return false;
	}

	private boolean scaleNode(Node node, float scale) {
		if (node != null) {
			Node parent = node.getParent();
			parent.removeChild(node);
			node.setLocalScale(node.getLocalScale().scaled(scale));
			parent.addChild(node);
			return true;
		}
		return false;
	}

	private void setupAugmentedImageDatabase() {
		Session session = arFragment.getArSceneView().getSession();
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
			return;
		}
		Config config = new Config(session);
		config.setAugmentedImageDatabase(augmentedImageDatabase);
		config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
		session.configure(config);
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

	private ArEditorViewModel obtainViewModel() {
		ARScene arScene = getIntent().getParcelableExtra(ARSCENE_KEY);
		Study study = getIntent().getParcelableExtra(KEY_STUDY);
		EditorState state = (EditorState) getIntent().getSerializableExtra(KEY_EDITOR_STATE);
		ArEditorViewModel viewModel = ViewModelProviders.of(this, createFactory()).get(ArEditorViewModel.class);
		viewModel.setArScene(arScene);
		viewModel.setStudy(study);
		viewModel.setEditorState(state);
		return viewModel;
	}

	private ArEditorViewModelFactory createFactory() {
		StudyDatabase database = StudyDatabase.getInstance(this);
		ArSceneDao arSceneDao = database.arSceneDao();
		ArObjectDao arImageToObjectRelationDao = database.arImageToObjectRelationDao();
		ArSceneRepository arSceneRepository = new ArSceneRepository(arSceneDao, arImageToObjectRelationDao);
		TestPersonDao testPersonDao = database.testPerson();
		TestPersonRepository testPersonRepository = new TestPersonRepository(testPersonDao);
		return new ArEditorViewModelFactory(getApplication(), arSceneRepository, testPersonRepository);
	}

	private void createARObject(Node parent, String imageName, Quaternion rotation, Vector3 scale) {
		String sjbFile = imageName.replace(FILE_TYPE_PNG, FILE_TYPE_SFB);
		ModelRenderable.builder().setSource(this, Uri.parse(sjbFile)).build().thenAccept(renderable -> {
			ArNode node = createArNode(sjbFile, renderable);
			node.setLocalRotation(rotation);
			node.setLocalScale(scale);
			node.setParent(parent);
			node.setOnTapListener((hitTestResult, motionEvent) -> onArNodeTapped(node));
			viewModel.getArNodes().add(node);
			node.setQrCodeNumber(viewModel.getArNodes().size());
			node.select();
			viewModel.setCurrentSelectedNode(node);
		}).exceptionally(throwable -> {
			Toast toast = Toast.makeText(this, "Laden der SFB Datei nicht möglich. Heißen Bilddatei und 3D Modell-Datei gleich?", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return null;
		});
	}

	private void onArNodeTapped(ArNode node) {
		node.select();
		viewModel.setCurrentSelectedNode(node);
		if (viewModel.getScaleTechnique().getValue().equals(ScaleTechnique.WIDGET_3D)) {
			attachScaleWidget(node);
		}
		if (viewModel.getRotationTechnique().getValue().equals(RotationTechnique.WIDGET_3D)) {
			attachRotateWidget(node);
		}
		if (viewModel.getEditorState().equals(EditorState.EDIT_MODE)) {
			attachDeleteWidget(node);
		}
	}

	private ArNode createArNode(String sjbFile, ModelRenderable renderable) {
		ArNode node = new ArNode(arFragment.getTransformationSystem(), sjbFile, renderable);
		node.getScaleController().setMaxScale(MAX_SCALE);
		node.getScaleController().setMinScale(MIN_SCALE);
		node.getScaleController().setSensitivity(SENSITIVITY_SCALE);
		return node;
	}


	private void attachRotateWidget(ArNode node) {
		rotateWidgetNode.setParent(node.getParent());
		rotateWidgetNode.setLocalPosition(node.getDown().scaled(0.5f));
	}

	private void attachScaleWidget(ArNode node) {
		scaleWidgetNode.setParent(node.getParent());
		scaleWidgetNode.setLocalPosition(node.getRight().scaled(0.35f));
	}

	private void attachDeleteWidget(ArNode node) {
		deleteWidgetNode.setParent(node.getParent());
		deleteWidgetNode.setLocalPosition(node.getDown().scaled(0.5f));
		deleteWidgetNode.setOnTapListener((hitTestResult, motionEvent) -> {
			viewModel.getArNodes().remove(node);
			node.getParent().removeChild(node);
			deleteWidgetNode.getParent().removeChild(deleteWidgetNode);
		});
	}

	@Override
	public void onDeleteAllArObjects() {
		viewModel.getArNodes().forEach(node -> {
			if (node.getParent() != null) {
				arFragment.getArSceneView().getScene().removeChild(node.getParent());
				arFragment.getArSceneView().getScene().removeChild(node);
			}
		});
		viewModel.setArNodes(new ArrayList<>());
	}

	@Override
	public void onEditorStateChanged(EditorState state) {
		viewModel.setEditorState(state);
		switch (state) {
			case EDIT_MODE:
				showEditModeFragment();
				break;
			case STUDY_MODE:
				removeDeleteWidget();
				showStudyModeFragment();
				break;
		}
	}
}
