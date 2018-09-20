package ncxp.de.arauthoringtool;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
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
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import ncxp.de.arauthoringtool.model.StudyDatabase;
import ncxp.de.arauthoringtool.model.dao.ArImageToObjectRelationDao;
import ncxp.de.arauthoringtool.model.dao.ArSceneDao;
import ncxp.de.arauthoringtool.model.dao.TestPersonDao;
import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.ArImageToObjectRelation;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.model.repository.TestPersonRepository;
import ncxp.de.arauthoringtool.sceneform.ArNode;
import ncxp.de.arauthoringtool.sceneform.DeleteWidgetNode;
import ncxp.de.arauthoringtool.sceneform.ImageAnchor;
import ncxp.de.arauthoringtool.sceneform.PlacementNode;
import ncxp.de.arauthoringtool.sceneform.RotateWidgetNode;
import ncxp.de.arauthoringtool.sceneform.ScaleWidgetNode;
import ncxp.de.arauthoringtool.ui.areditor.ArEditFragment;
import ncxp.de.arauthoringtool.ui.areditor.ArInteractionListener;
import ncxp.de.arauthoringtool.ui.areditor.ArStudyFragment;
import ncxp.de.arauthoringtool.ui.areditor.util.EditorState;
import ncxp.de.arauthoringtool.ui.areditor.util.RotationTechnique;
import ncxp.de.arauthoringtool.ui.areditor.util.ScaleTechnique;
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
	private static final float  SENSITIVITY_SCALE  = 0.5f;

	private ArFragment        arFragment;
	private ArEditFragment    editFragment;
	private ArStudyFragment   studyFragment;
	private ModelRenderable   frameRenderable;
	private ArEditorViewModel viewModel;
	private DeleteWidgetNode  deleteWidgetNode;
	private ScaleWidgetNode   scaleWidgetNode;
	private RotateWidgetNode  rotateWidgetNode;
	private Material          highlight;
	private ImageView         crosshair;
	private GestureDetector   trackableGestureDetector;
	private DisplayMetrics    metrics;
	private float             displayCenterY;
	private float             displayCenterX;
	private Camera            camera;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_marker_fragment);
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		displayCenterX = metrics.widthPixels / 2;
		displayCenterY = metrics.heightPixels / 2;
		crosshair = findViewById(R.id.crosshair);
		setupArFragment();
		camera = arFragment.getArSceneView().getScene().getCamera();
		viewModel = obtainViewModel(this);
		initPlacingFrame();
		initDeleteWidgetNode();
		initRotateWidgetNode();
		initScaleWidgetNode();
		setupAugmentedImageDatabase();
		setupSelectionTechnique();
		setupRotationTechnique();
		setupScaleTechnique();
		viewModel.resetInteractionTechnique();
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
				case CROSSHAIR:
					onCrosshairTechnique();
					break;
				case NONE:
					onNoneSelectionTechnique();
					break;
			}

		});
	}

	private void onCrosshairTechnique() {
		crosshair.setVisibility(View.VISIBLE);
		trackableGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent motionEvent) {

				HitTestResult hitTestResult = arFragment.getArSceneView()
														.getScene()
														.hitTest(projectRay(displayCenterX,
																			displayCenterY,
																			metrics.widthPixels,
																			metrics.heightPixels,
																			camera.getProjectionMatrix().data,
																			camera.getViewMatrix().data));
				if (hitTestResult.getNode() != null) {
					ArNode node = (ArNode) hitTestResult.getNode();
					arFragment.getTransformationSystem().getSelectionVisualizer().applySelectionVisual(node);
					node.select();
				} else {
					arFragment.getTransformationSystem().getSelectionVisualizer().removeSelectionVisual(arFragment.getTransformationSystem().getSelectedNode());
				}
				return true;
			}
		});
		arFragment.getArSceneView().getScene().setOnPeekTouchListener(((hitTestResult, motionEvent) -> trackableGestureDetector.onTouchEvent(motionEvent)));
	}

	private void onRaycastingTechnique() {
		crosshair.setVisibility(View.GONE);
		arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
			viewModel.setCurrentSelectedNode(null);
			removeDeleteWidget();
		});
		arFragment.getArSceneView().getScene().setOnPeekTouchListener((hitTestResult, motionEvent) -> arFragment.onPeekTouch(hitTestResult, motionEvent));
		trackableGestureDetector = null;
	}

	private void onNoneSelectionTechnique() {
		arFragment.getArSceneView().getScene().setOnPeekTouchListener(null);
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
		viewModel.getAugmentedImageMap().values().stream().filter(node -> node instanceof ArNode).forEach(node -> {
			ArNode arNode = ((ArNode) node);
			arNode.getRotationController().setEnabled(true);
		});
	}

	private void onNoneRotationTechnique() {
		viewModel.getAugmentedImageMap().values().stream().filter(node -> node instanceof ArNode).forEach(node -> ((ArNode) node).getRotationController().setEnabled(false));
	}

	private void setupScaleTechnique() {
		viewModel.getScaleTechnique().observe(this, technique -> {
			switch (technique) {
				case PINCH:
					onPinchTechnique();
					break;
				case WIDGET_3D:
				case NONE:
					onNoneScaleTechnique();
					break;
			}
			removeScaleWidget();
		});
	}

	private void onPinchTechnique() {
		viewModel.getAugmentedImageMap().values().stream().filter(node -> node instanceof ArNode).forEach(node -> {
			ArNode arNode = ((ArNode) node);
			arNode.getScaleController().setEnabled(true);
		});
	}

	private void onNoneScaleTechnique() {
		viewModel.getAugmentedImageMap().values().stream().filter(node -> node instanceof ArNode).forEach(node -> ((ArNode) node).getScaleController().setEnabled(false));
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
		arFragment.getArSceneView().getScene().setOnUpdateListener(this::onUpdateFrame);
		arFragment.getPlaneDiscoveryController().hide();
		arFragment.getPlaneDiscoveryController().setInstructionView(null);
		Texture.Sampler sampler = Texture.Sampler.builder()
												 .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
												 .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
												 .setWrapMode(Texture.Sampler.WrapMode.REPEAT)
												 .build();

		CompletableFuture<Texture> hexagon = Texture.builder().setSource(this, R.drawable.hexagon).setSampler(sampler).build();
		arFragment.getArSceneView().getPlaneRenderer().getMaterial().thenAcceptBoth(hexagon, (material, texture) -> {
			material.setTexture(PlaneRenderer.MATERIAL_TEXTURE, texture);
			material.setFloat3(PlaneRenderer.MATERIAL_COLOR, new Color(1, 1, 1, 0.1f));
		});
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
		if (!viewModel.containsAugmentedImage(augmentedImage)) {
			ImageAnchor imageAnchor = new ImageAnchor();
			imageAnchor.setImage(augmentedImage);

			if (viewModel.containsArSceneObject(augmentedImage.getName())) {
				ArImageToObjectRelation arImageToObjectRelation = viewModel.getArSceneObjectFileName(augmentedImage.getName());
				createARObject(imageAnchor, arImageToObjectRelation.getImageName(), arImageToObjectRelation.getRotation(), arImageToObjectRelation.getScale());
			} else {
				attachNewPlaceholder(imageAnchor);
				viewModel.addARObject(augmentedImage.getName(), imageAnchor);
			}
			arFragment.getArSceneView().getScene().addChild(imageAnchor);
		}
	}

	private PlacementNode attachNewPlaceholder(Node parent) {
		PlacementNode placeholder = new PlacementNode(frameRenderable, highlight);
		placeholder.setOnTapListener((hitTestResult, motionEvent) -> {
			Node currentSelectedNode = viewModel.getCurrentSelectedNode().getValue();
			if (currentSelectedNode instanceof PlacementNode && !viewModel.getCurrentSelectedNode().equals(placeholder)) {
				((PlacementNode) currentSelectedNode).unselect();
			}
			viewModel.setCurrentSelectedNode(placeholder);
			placeholder.select();

		});
		placeholder.setParent(parent);
		return placeholder;
	}


	private void initPlacingFrame() {
		ModelRenderable.builder().setSource(this, R.raw.frame).build().thenAccept(renderable -> {
			MaterialFactory.makeOpaqueWithColor(this, new Color(0, 1f, 0)).thenAccept(material -> {
				highlight = material;
				frameRenderable = renderable;
			});
		});
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

	public static ArEditorViewModel obtainViewModel(FragmentActivity activity) {
		ARScene arScene = activity.getIntent().getParcelableExtra(ARSCENE_KEY);
		Study study = activity.getIntent().getParcelableExtra(KEY_STUDY);
		EditorState state = (EditorState) activity.getIntent().getSerializableExtra(KEY_EDITOR_STATE);
		ArEditorViewModel viewModel = ViewModelProviders.of(activity, createFactory(activity)).get(ArEditorViewModel.class);
		viewModel.setArScene(arScene);
		viewModel.setStudy(study);
		viewModel.setEditorState(state);
		return viewModel;
	}

	private static ArEditorViewModelFactory createFactory(FragmentActivity activity) {
		StudyDatabase database = StudyDatabase.getInstance(activity);
		ArSceneDao arSceneDao = database.arSceneDao();
		ArImageToObjectRelationDao arImageToObjectRelationDao = database.arImageToObjectRelationDao();
		ArSceneRepository arSceneRepository = new ArSceneRepository(arSceneDao, arImageToObjectRelationDao);
		TestPersonDao testPersonDao = database.testPerson();
		TestPersonRepository testPersonRepository = new TestPersonRepository(testPersonDao);
		return new ArEditorViewModelFactory(activity.getApplication(), arSceneRepository, testPersonRepository);
	}

	private void createARObject(Node parent, String imageName, Quaternion rotation, Vector3 scale) {
		String sjbFile = imageName.replace(FILE_TYPE_PNG, FILE_TYPE_SFB);
		ModelRenderable.builder().setSource(this, Uri.parse(sjbFile)).build().thenAccept(renderable -> {
			ArNode node = createArNode(sjbFile, renderable);
			node.setLocalRotation(rotation);
			node.setLocalScale(scale);
			node.setParent(parent);
			node.setOnTapListener((hitTestResult, motionEvent) -> onArNodeTapped(node));
			if (parent instanceof ImageAnchor) {
				ImageAnchor imageAnchor = (ImageAnchor) parent;
				viewModel.addARObject(imageAnchor.getImage().getName(), node);
			}
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
		rotateWidgetNode.setLocalPosition(node.getForward().scaled(0.2f));
	}

	private void attachScaleWidget(ArNode node) {
		scaleWidgetNode.setParent(node.getParent());
		scaleWidgetNode.setLocalPosition(node.getRight().scaled(0.35f));
	}

	private void attachDeleteWidget(Node node) {
		deleteWidgetNode.setParent(node.getParent());
		deleteWidgetNode.setLocalPosition(node.getRight().scaled(0.1f));
		deleteWidgetNode.setOnTapListener((hitTestResult, motionEvent) -> {
			ImageAnchor imageAnchor = (ImageAnchor) node.getParent();
			imageAnchor.removeChild(node);
			attachNewPlaceholder(imageAnchor);
			deleteWidgetNode.getParent().removeChild(deleteWidgetNode);
		});
	}


	private void replaceARObject(String imageName, Node parent) {
		Node anchor;
		if (parent != null && parent.getParent() != null) {
			anchor = parent.getParent();
			anchor.removeChild(parent);
		} else {
			Node currentSelection = viewModel.getCurrentSelectedNode().getValue();
			anchor = currentSelection.getParent();
			anchor.removeChild(currentSelection);
		}
		createARObject(anchor, imageName, Quaternion.identity(), Vector3.one());
	}


	@Override
	public void onReplaceArObject(String imageName, Node node) {
		replaceARObject(imageName, node);
	}

	@Override
	public void onDeleteAllArObjects() {
		viewModel.getAugmentedImageMap().forEach((key, value) -> {
			if (value.getParent() != null) {
				arFragment.getArSceneView().getScene().removeChild(value.getParent());
				arFragment.getArSceneView().getScene().removeChild(value);
			}
		});
		viewModel.setAugmentedImageMap(new HashMap<>());
	}

	@Override
	public void onEditorStateChanged(EditorState state) {
		viewModel.setEditorState(state);
		switch (state) {
			case EDIT_MODE:
				viewModel.setComingFromStudyModus(true);
				showEditModeFragment();
				break;
			case STUDY_MODE:
				removeDeleteWidget();
				viewModel.setComingFromStudyModus(false);
				showStudyModeFragment();
				break;
		}
	}

	public static Ray projectRay(float tapX, float tapY, float screenWidth, float screenHeight, float[] projectionMatrix, float[] viewMatrix) {
		float[] viewProjMtx = new float[16];
		Matrix.multiplyMM(viewProjMtx, 0, projectionMatrix, 0, viewMatrix, 0);
		return screenPointToRay(tapX, tapY, screenWidth, screenHeight, viewProjMtx);
	}

	public static Ray screenPointToRay(float tapX, float tapY, float screenWidth, float screenHeight, float[] viewProjMtx) {
		tapY = screenHeight - tapY;
		float x = tapX * 2.0F / screenWidth - 1.0F;
		float y = tapY * 2.0F / screenHeight - 1.0F;
		float[] farScreenPoint = new float[]{x, y, 1.0F, 1.0F};
		float[] nearScreenPoint = new float[]{x, y, -1.0F, 1.0F};
		float[] nearPlanePoint = new float[4];
		float[] farPlanePoint = new float[4];
		float[] invertedProjectionMatrix = new float[16];
		Matrix.setIdentityM(invertedProjectionMatrix, 0);
		Matrix.invertM(invertedProjectionMatrix, 0, viewProjMtx, 0);
		Matrix.multiplyMV(nearPlanePoint, 0, invertedProjectionMatrix, 0, nearScreenPoint, 0);
		Matrix.multiplyMV(farPlanePoint, 0, invertedProjectionMatrix, 0, farScreenPoint, 0);
		Vector3 direction = new Vector3(farPlanePoint[0] / farPlanePoint[3], farPlanePoint[1] / farPlanePoint[3], farPlanePoint[2] / farPlanePoint[3]);
		Vector3 origin = new Vector3(new Vector3(nearPlanePoint[0] / nearPlanePoint[3], nearPlanePoint[1] / nearPlanePoint[3], nearPlanePoint[2] / nearPlanePoint[3]));
		direction = new Vector3(direction.x - origin.x, direction.y - origin.y, direction.z - origin.z);
		direction.normalized();
		return new Ray(origin, direction);
	}
}
