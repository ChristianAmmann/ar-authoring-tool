package ncxp.de.mobiledatacollection;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import com.google.ar.sceneform.ux.TransformationSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.ArImageToObjectRelationDao;
import ncxp.de.mobiledatacollection.model.dao.ArSceneDao;
import ncxp.de.mobiledatacollection.model.data.ARScene;
import ncxp.de.mobiledatacollection.model.data.ArImageToObjectRelation;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.sceneform.AugmentedImageAnchor;
import ncxp.de.mobiledatacollection.sceneform.DeleteNode;
import ncxp.de.mobiledatacollection.sceneform.ObjectARImageNode;
import ncxp.de.mobiledatacollection.sceneform.PlaceholderNode;
import ncxp.de.mobiledatacollection.sceneform.RotateWidgetNode;
import ncxp.de.mobiledatacollection.sceneform.ScaleWidgetNode;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArEditFragment;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModel;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModelFactory;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArInteractionListener;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArStudyFragment;
import ncxp.de.mobiledatacollection.ui.arimagemarker.EditorState;
import ncxp.de.mobiledatacollection.ui.arimagemarker.RotationTechnique;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ScaleTechnique;

import static ncxp.de.mobiledatacollection.ui.studies.viewmodel.StudiesViewModel.DIRECTORY;

public class ArImageMarkerActivity extends AppCompatActivity implements ArInteractionListener {

	private static final String TAG                              = ArImageMarkerActivity.class.getCanonicalName();
	public static final  String ARSCENE_KEY                      = "arscene_key";
	public static final  String KEY_STUDY                        = "study_key";
	public static final  String KEY_EDITOR_STATE                 = "editor_state_key";
	private static final String FILE_TYPE_SFB                    = "sfb";
	private static final String FILE_TYPE_PNG                    = "png";
	public static final  String AUGMENTED_IMAGE_DB               = "markers.imgdb";
	private static final int    PERMISSION_CODE_SCREEN_CAPTURING = 4123;

	private ArFragment           arFragment;
	private TransformationSystem transformationSystem;
	private ModelRenderable      frameRenderable;


	private ArImageMarkerViewModel viewModel;
	private DeleteNode             deleteNode;
	private ScaleWidgetNode        scaleWidgetNode;
	private RotateWidgetNode       rotateWidgetNode;
	private Material               highlight;
	private ImageView              crosshair;
	private GestureDetector        trackableGestureDetector;

	private MediaProjection         mMediaProjection;
	private MediaProjectionCallback mMediaProjectionCallback;

	private MediaRecorder          mMediaRecorder;
	private int                    mScreenDensity;
	private MediaProjectionManager mProjectionManager;
	DisplayMetrics metrics;
	private float  displayCenterY;
	private float  displayCenterX;
	private Camera camera;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_marker_fragment);
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenDensity = metrics.densityDpi;
		displayCenterX = metrics.widthPixels / 2;
		displayCenterY = metrics.heightPixels / 2;
		mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
		//TODO
		//initRecorder();
		crosshair = findViewById(R.id.crosshair);
		setupArFragment();
		camera = arFragment.getArSceneView().getScene().getCamera();
		viewModel = obtainViewModel(this);
		initPlacing();
		setupAugmentedImageDatabase();
		setupInteractionTechnique();
		if (viewModel.getState().equals(EditorState.EDIT_MODE)) {
			viewModel.resetInteractionTechnique();
			showEditModeFragment();
		} else {
			showStudyModeFragment();
		}

	}

	private void setupInteractionTechnique() {
		//TODO Refactor
		viewModel.getSelectionTechnique().observe(this, technique -> {
			// TODO remove selectNode not working
			switch (technique) {
				case CROSSHAIR:
					crosshair.setVisibility(View.VISIBLE);
					arFragment.getArSceneView().getScene().setOnPeekTouchListener(((hitTestResult, motionEvent) -> trackableGestureDetector.onTouchEvent(motionEvent)));

					trackableGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
						@Override
						public boolean onSingleTapUp(MotionEvent e) {

							HitTestResult hitTestResult = arFragment.getArSceneView()
																	.getScene()
																	.hitTest(projectRay(displayCenterX,
																						displayCenterY,
																						metrics.widthPixels,
																						metrics.heightPixels,
																						camera.getProjectionMatrix().data,
																						camera.getViewMatrix().data));

							return true;
						}
					});
					break;
				case RAYCASTING:
					crosshair.setVisibility(View.GONE);
					arFragment.getArSceneView().getScene().setOnPeekTouchListener((hitTestResult, motionEvent) -> arFragment.onPeekTouch(hitTestResult, motionEvent));
					trackableGestureDetector = null;
					break;
			}
		});
		viewModel.getRotationTechnique().observe(this, technique -> {
			switch (technique) {
				case TWO_FINGER:
					break;
				case WIDGET_3D:
					break;
			}

		});
		viewModel.getScaleTechnique().observe(this, technique -> {
			switch (technique) {
				case PINCH:
					break;
				case WIDGET_3D:
					break;
			}
		});
	}

	private void showEditModeFragment() {
		getSupportFragmentManager().beginTransaction().replace(R.id.editor_container, ArEditFragment.newInstance(), null).commit();
	}

	private void showStudyModeFragment() {
		getSupportFragmentManager().beginTransaction().replace(R.id.editor_container, ArStudyFragment.newInstance(), null).commit();
	}

	private void setupArFragment() {
		arFragment.getArSceneView().getScene().setOnUpdateListener(this::onUpdateFrame);
		arFragment.getPlaneDiscoveryController().hide();
		arFragment.getPlaneDiscoveryController().setInstructionView(null);
		transformationSystem = arFragment.getTransformationSystem();
		Texture.Sampler sampler = Texture.Sampler.builder()
												 .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
												 .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
												 .setWrapMode(Texture.Sampler.WrapMode.REPEAT)
												 .build();

		CompletableFuture<Texture> hexagon = Texture.builder().setSource(this, R.drawable.hexagon).setSampler(sampler).build();
		arFragment.getArSceneView().getPlaneRenderer().getMaterial().thenAcceptBoth(hexagon, (material, texture) -> material.setTexture(PlaneRenderer.MATERIAL_TEXTURE, texture));
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
			AugmentedImageAnchor imageAnchor = new AugmentedImageAnchor();
			imageAnchor.setImage(augmentedImage);

			if (viewModel.containsArSceneObject(augmentedImage.getName())) {
				ArImageToObjectRelation arImageToObjectRelation = viewModel.getArSceneObjectFileName(augmentedImage.getName());
				createObjectARImageNode(imageAnchor, arImageToObjectRelation);
			} else {
				attachNewPlaceholder(imageAnchor);
				viewModel.addARObject(augmentedImage.getName(), imageAnchor);
			}
			arFragment.getArSceneView().getScene().addChild(imageAnchor);
		}
	}

	private PlaceholderNode attachNewPlaceholder(Node parent) {
		PlaceholderNode placeholder = new PlaceholderNode(frameRenderable, highlight);
		placeholder.setOnTapListener((hitTestResult, motionEvent) -> {
			//TODO showBottomThumbnails();
			Node currentSelectedNode = viewModel.getCurrentSelectedNode().getValue();
			if (currentSelectedNode instanceof PlaceholderNode && !viewModel.getCurrentSelectedNode().equals(placeholder)) {
				((PlaceholderNode) currentSelectedNode).unselect();
			}
			viewModel.setCurrentSelectedNode(placeholder);
			placeholder.select();

		});
		placeholder.setParent(parent);
		return placeholder;
	}

	private void initPlacing() {
		ModelRenderable.builder().setSource(this, R.raw.frame).build().thenAccept(renderable -> {
			MaterialFactory.makeOpaqueWithColor(this, new Color(0, 1f, 0)).thenAccept(material -> {
				highlight = material;
				frameRenderable = renderable;
			});
		});
		ViewRenderable.builder().setView(this, R.layout.delete_view).build().thenAccept((renderable) -> deleteNode = new DeleteNode(renderable));
		ViewRenderable.builder().setView(this, R.layout.rotation_widget_controls).build().thenAccept(viewRenderable -> {
			rotateWidgetNode = new RotateWidgetNode(viewRenderable);
			View widgetView = viewRenderable.getView();
			ImageButton rotateRightButton = widgetView.findViewById(R.id.rotate_right);
			ImageButton rotateLeftButton = widgetView.findViewById(R.id.rotate_left);
			// TODO rotateRightButton.setOnDragListener();
			rotateRightButton.setOnClickListener(view -> rotateNode(viewModel.getCurrentSelectedNode().getValue(), 15f));
			rotateLeftButton.setOnClickListener(view -> rotateNode(viewModel.getCurrentSelectedNode().getValue(), -15f));

		});
		ViewRenderable.builder().setView(this, R.layout.scale_widget_controls).build().thenAccept(viewRenderable -> {
			scaleWidgetNode = new ScaleWidgetNode(viewRenderable);
			View widgetView = viewRenderable.getView();
			ImageButton scaleUpButton = widgetView.findViewById(R.id.scale_up);
			ImageButton scaleDownButton = widgetView.findViewById(R.id.scale_down);
			scaleUpButton.setOnClickListener(view -> {
				//TODO scale selected Node up

			});
			scaleDownButton.setOnClickListener(view -> {
				//TODO scale selected node down
			});

		});
	}

	private void rotateNode(Node node, float degrees) {
		if (node != null) {
			Vector3 axis = new Vector3(1, 1, 0);
			Quaternion currentRotation = node.getWorldRotation();
			Quaternion addRotation = Quaternion.axisAngle(axis, degrees);
			Quaternion newRotation = Quaternion.multiply(currentRotation, addRotation);
			node.setLocalRotation(newRotation);
		}
	}

	private boolean setupAugmentedImageDatabase() {
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

	public static ArImageMarkerViewModel obtainViewModel(FragmentActivity activity) {
		ARScene arScene = activity.getIntent().getParcelableExtra(ARSCENE_KEY);
		Study study = activity.getIntent().getParcelableExtra(KEY_STUDY);
		EditorState state = (EditorState) activity.getIntent().getSerializableExtra(KEY_EDITOR_STATE);
		ArImageMarkerViewModel viewModel = ViewModelProviders.of(activity, createFactory(activity)).get(ArImageMarkerViewModel.class);
		viewModel.setArScene(arScene);
		viewModel.setStudy(study);
		viewModel.setState(state);
		return viewModel;
	}

	private static ArImageMarkerViewModelFactory createFactory(FragmentActivity activity) {
		StudyDatabase database = StudyDatabase.getInstance(activity);
		ArSceneDao arSceneDao = database.arSceneDao();
		ArImageToObjectRelationDao arImageToObjectRelationDao = database.arImageToObjectRelationDao();
		ArSceneRepository arSceneRepository = new ArSceneRepository(arSceneDao, arImageToObjectRelationDao);
		return new ArImageMarkerViewModelFactory(activity.getApplication(), arSceneRepository);
	}

	private void createObjectARImageNode(Node parent, ArImageToObjectRelation arImageToObjectRelation) {
		String sjbFile = arImageToObjectRelation.getImageName().replace(FILE_TYPE_PNG, FILE_TYPE_SFB);
		ModelRenderable.builder().setSource(this, Uri.parse(sjbFile)).build().thenAccept(renderable -> {
			ObjectARImageNode node = new ObjectARImageNode(transformationSystem, sjbFile, renderable, highlight);
			node.setLocalRotation(arImageToObjectRelation.getRotation());
			node.setLocalScale(arImageToObjectRelation.getScale());
			node.setParent(parent);
			node.setOnTapListener((hitTestResult, motionEvent) -> {
				//TODO figure best deletion method
				//attachDeleteNode(node);
				node.select();
				if (viewModel.getScaleTechnique().getValue().equals(ScaleTechnique.WIDGET_3D)) {
					attachScaleWidget(node);
				}
				if (viewModel.getRotationTechnique().getValue().equals(RotationTechnique.WIDGET_3D)) {
					attachRotateWidget(node);
				}
				viewModel.setCurrentSelectedNode(node);
			});
			if (parent instanceof AugmentedImageAnchor) {
				AugmentedImageAnchor augmentedImageAnchor = (AugmentedImageAnchor) parent;
				viewModel.addARObject(augmentedImageAnchor.getImage().getName(), node);
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

	private void attachRotateWidget(ObjectARImageNode node) {
		rotateWidgetNode.setParent(node);
		rotateWidgetNode.setLocalPosition(node.getUp().scaled(0.2f));
	}

	private void attachScaleWidget(ObjectARImageNode node) {
		scaleWidgetNode.setParent(node);
		scaleWidgetNode.setLocalPosition(node.getRight().scaled(0.35f));
	}

	private void createObjectARImageNode(Node parent, String imageName) {
		//TODO Refactor Redundant code
		String sjbFile = imageName.replace("png", "sfb");
		ModelRenderable.builder().setSource(this, Uri.parse(sjbFile)).build().thenAccept(renderable -> {
			ObjectARImageNode node = new ObjectARImageNode(transformationSystem, sjbFile, renderable, highlight);
			node.setParent(parent);
			node.setOnTapListener((hitTestResult, motionEvent) -> {
				//TODO figure best deletion method
				//attachDeleteNode(node);
				node.select();
				viewModel.setCurrentSelectedNode(node);
			});
			if (parent instanceof AugmentedImageAnchor) {
				AugmentedImageAnchor augmentedImageAnchor = (AugmentedImageAnchor) parent;
				viewModel.addARObject(augmentedImageAnchor.getImage().getName(), node);
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

	private void replaceObjectAR(String imageName, Node parent) {
		Node anchor;
		if (parent != null) {
			anchor = parent.getParent();
			anchor.removeChild(parent);
		} else {
			Node currentSelection = viewModel.getCurrentSelectedNode().getValue();
			anchor = currentSelection.getParent();
			anchor.removeChild(currentSelection);
		}
		createObjectARImageNode(anchor, imageName);
	}


	private void attachDeleteNode(Node node) {
		deleteNode.setParent(node);
		deleteNode.setOnTapListener((hitTestResult, motionEvent) -> {
			AugmentedImageAnchor imageAnchor = (AugmentedImageAnchor) node.getParent();
			imageAnchor.removeChild(node);
			attachNewPlaceholder(imageAnchor);
			//TODO
			//showActionButtons();
		});
	}

	@Override
	public void onReplaceArObject(String imageName, Node node) {
		replaceObjectAR(imageName, node);
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
	public void onEditorStateChanged() {
		if (viewModel.getState().equals(EditorState.EDIT_MODE)) {
			showEditModeFragment();
		} else {
			showStudyModeFragment();
		}
	}

	private class MediaProjectionCallback extends MediaProjection.Callback {
		@Override
		public void onStop() {
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			mMediaProjection = null;
		}

	}

	private void prepareRecorder() {
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			finish();
		}
	}

	private void initRecorder() {
		if (!isAudioRecordingGranted()) {
			return;
		}

		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
			mMediaRecorder.setVideoFrameRate(30);
			mMediaRecorder.setVideoSize(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
			mMediaRecorder.setOutputFile(getFilePath());
			prepareRecorder();
		}
	}

	private boolean isAudioRecordingGranted() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			requestReccordAudioPermission();
			return false;
		}
		return true;
	}

	private void requestReccordAudioPermission() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE_SCREEN_CAPTURING);
	}


	public String getFilePath() {
		File exportDir = new File(Environment.getExternalStorageDirectory(), DIRECTORY);
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		File studyDir = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY, viewModel.getStudy().getName());
		if (!studyDir.exists()) {
			studyDir.mkdirs();
		}

		String videoName = "capture_test.mp4";
		return studyDir.getPath() + File.separator + videoName;
	}

	private VirtualDisplay createVirtualDisplay() {
		return mMediaProjection.createVirtualDisplay("MainActivity",
													 this.getResources().getDisplayMetrics().widthPixels,
													 this.getResources().getDisplayMetrics().heightPixels,
													 mScreenDensity,
													 DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
													 mMediaRecorder.getSurface(),
													 null,
													 null);
	}

	/*
	private void onSingleTap(MotionEvent motionEvent) {
		Frame frame = arFragment.getArSceneView().getArFrame();
		if (frame != null && motionEvent != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
			for (HitResult hit : frame.hitTest(motionEvent)) {
				Trackable trackable = hit.getTrackable();
				if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
					Plane plane = (Plane) trackable;

					// Handle plane hits.
					break;
				} else if (trackable instanceof Point) {
					// Handle point hits
					Point point = (Point) trackable;

				} else if (trackable instanceof AugmentedImage) {
					// Handle image hits.
					AugmentedImage image = (AugmentedImage) trackable;
				}
			}
		}
	}

	private MotionEvent getMotionEventCenter(MotionEvent event) {
		return MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), displayCenterX, displayCenterY, event.getMetaState());
	}

	*/

	public static Vector3 GetWorldCoords(float tapX, float tapY, float screenWidth, float screenHeight, float[] projectionMatrix, float[] viewMatrix) {
		Ray touchRay = projectRay(tapX, tapY, screenWidth, screenHeight, projectionMatrix, viewMatrix);
		return touchRay.getOrigin();
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
