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
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArEditFragment;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModel;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArImageMarkerViewModelFactory;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArInteractionListener;
import ncxp.de.mobiledatacollection.ui.arimagemarker.ArStudyFragment;
import ncxp.de.mobiledatacollection.ui.arimagemarker.EditorState;

import static ncxp.de.mobiledatacollection.ui.studies.viewmodel.StudiesViewModel.DIRECTORY;

public class ArImageMarkerActivity extends AppCompatActivity implements ArInteractionListener {

	private static final String TAG                              = ArImageMarkerActivity.class.getCanonicalName();
	public static final  String ARSCENE_KEY                      = "arscene_key";
	public static final  String KEY_STUDY                        = "study_key";
	public static final  String KEY_EDITOR_STATE                 = "editor_state_key";
	private static final String FILE_TYPE                        = ".sfb";
	public static final  String AUGMENTED_IMAGE_DB               = "markers.imgdb";
	private static final int    PERMISSION_CODE_SCREEN_CAPTURING = 4123;

	private ArFragment           arFragment;
	private TransformationSystem transformationSystem;
	private ModelRenderable      frameRenderable;


	private ArImageMarkerViewModel viewModel;
	private DeleteNode             deleteNode;
	private Material               highlight;

	private MediaProjection         mMediaProjection;
	private MediaProjectionCallback mMediaProjectionCallback;
	private Study                   study;

	private MediaRecorder          mMediaRecorder;
	private int                    mScreenDensity;
	private MediaProjectionManager mProjectionManager;

	private float displayCenterY;
	private float displayCenterX;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arimage_marker);
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_marker_fragment);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenDensity = metrics.densityDpi;
		mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
		//TODO
		//initRecorder();
		setupArFragment();
		viewModel = obtainViewModel(this);
		initPlacing();
		setupAugmentedImageDatabase();

		if (viewModel.getState().equals(EditorState.EDIT_MODE)) {
			showEditModeFragment();
		} else {
			showStudyModeFragment();
		}

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
		//TODO Outline shader
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
				ArImageToObjectRelation arSceneObjectFileName = viewModel.getArSceneObjectFileName(augmentedImage.getName());
				createObjectARImageNode(imageAnchor, arSceneObjectFileName.getImageName());
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

	private void createObjectARImageNode(Node parent, String imageName) {
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
		File studyDir = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY, study.getName());
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
}
