package ncxp.de.mobiledatacollection;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.IOException;

import ncxp.de.mobiledatacollection.datalogger.SensorBackgroundService;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.TestPersonState;

import static ncxp.de.mobiledatacollection.ui.studies.viewmodel.StudiesViewModel.DIRECTORY;

public class ArActivity extends AppCompatActivity {
	private static final String TAG = ArActivity.class.getSimpleName();

	public static final  String KEY_STUDY                        = "study_key";
	private static final int    PERMISSION_CODE_SCREEN_CAPTURING = 4123;

	private ArFragment      arFragment;
	private ModelRenderable andyRenderable;
	private GestureDetector trackableGestureDetector;

	private ImageButton             settingsButton;
	private ImageButton             expandBottomToolbarButton;
	private ImageButton             testModusButton;
	private ImageButton             addSubjectButton;
	private ImageButton             cancelButton;
	private ImageButton             playAndPauseButton;
	private ImageButton             finishButton;
	private ImageView               timeIcon;
	private LinearLayout            bottomToolbar;
	private TestPersonState         state = TestPersonState.STOPPED;
	private TextView                studyStatusView;
	private Chronometer             chronometer;
	private float                   displayCenterY;
	private float                   displayCenterX;
	private Study                   study;
	private SensorBackgroundService sensorBackgroundService;
	private boolean                 bound = false;
	private int                     mScreenDensity;
	private MediaProjectionManager  mProjectionManager;
	private MediaProjection         mMediaProjection;
	private VirtualDisplay          mVirtualDisplay;
	private MediaProjectionCallback mMediaProjectionCallback;
	private MediaRecorder           mMediaRecorder;


	@Override
	@SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
	// CompletableFuture requires api level 24
	// FutureReturnValueIgnored is not valid
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar);
		if (getIntent() != null) {
			study = getIntent().getParcelableExtra(KEY_STUDY);
		}
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenDensity = metrics.densityDpi;
		mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
		initRecorder();
		initView();
		initBottomBar();
		View decorView = getWindow().getDecorView();
		decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
			if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
				decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
						.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
		});
		chronometer.setOnChronometerTickListener(chronometerChanged -> chronometer = chronometerChanged);
		settingsButton.setOnClickListener(this::onSettingsClicked);
		addSubjectButton.setOnClickListener(this::onAddSubjectClicked);
		playAndPauseButton.setOnClickListener(this::onPlayAndPauseClicked);
		cancelButton.setOnClickListener(this::onCancelClicked);
		finishButton.setOnClickListener(this::onFinishClicked);

		testModusButton.setOnClickListener(this::onTestModusClicked);
		displayCenterX = getResources().getDisplayMetrics().widthPixels / 2 - 100;
		displayCenterY = getResources().getDisplayMetrics().heightPixels / 2;

		// When you build a Renderable, Sceneform loads its resources in the background while returning
		// a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
		ModelRenderable.builder().setSource(this, R.raw.andy).build().thenAccept(renderable -> andyRenderable = renderable).exceptionally(throwable -> {
			Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return null;
		});


		arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
			if (andyRenderable == null) {
				return;
			}

			if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
				return;
			}

			// Create the Anchor.
			Anchor anchor = hitResult.createAnchor();
			AnchorNode anchorNode = new AnchorNode(anchor);
			anchorNode.setParent(arFragment.getArSceneView().getScene());

			// Create the transformable andy and add it to the anchor.
			TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
			andy.setParent(anchorNode);
			andy.setRenderable(andyRenderable);
			andy.select();
		});


		arFragment.getArSceneView().getScene().setOnPeekTouchListener(this::handleOnTouch);
		trackableGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent motionEvent) {
				//TODO check selection technique
				onSingleTap(motionEvent);
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}
		});
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


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PERMISSION_CODE_SCREEN_CAPTURING:
				if (resultCode == RESULT_OK) {
					initRecorder();
					mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
					mMediaProjection.registerCallback(mMediaProjectionCallback, null);
					mVirtualDisplay = createVirtualDisplay();
					mMediaRecorder.start();
				}
				break;
		}
	}

	private VirtualDisplay createVirtualDisplay() {
		return mMediaProjection.createVirtualDisplay("MainActivity",
													 this.getResources().getDisplayMetrics().widthPixels,
													 this.getResources().getDisplayMetrics().heightPixels,
													 mScreenDensity,
													 DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
													 mMediaRecorder.getSurface(),
													 null /*Callbacks*/,
													 null /*Handler*/);
	}

	private void initView() {
		expandBottomToolbarButton = findViewById(R.id.expand_bottom_toolbar_button);
		settingsButton = findViewById(R.id.ar_settings_button);
		addSubjectButton = findViewById(R.id.add_subject_button);
		bottomToolbar = findViewById(R.id.bottom_toolbar);
		playAndPauseButton = findViewById(R.id.play_and_pause);
		finishButton = findViewById(R.id.finish);
		cancelButton = findViewById(R.id.cancel);
		testModusButton = findViewById(R.id.test_modus);
		timeIcon = findViewById(R.id.timer_icon);
		studyStatusView = findViewById(R.id.study_status);
		chronometer = findViewById(R.id.time_view);
		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
	}

	private void initBottomBar() {
		expandBottomToolbarButton.setOnClickListener((view) -> {
			int visibility = bottomToolbar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
			CoordinatorLayout.LayoutParams paramsExpand = (CoordinatorLayout.LayoutParams) expandBottomToolbarButton.getLayoutParams();
			if (visibility == View.GONE) {
				paramsExpand.setAnchorId(R.id.ux_fragment);
				paramsExpand.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
				expandBottomToolbarButton.setImageResource(R.drawable.chevron_up);
			} else {
				paramsExpand.setAnchorId(R.id.bottom_toolbar);
				paramsExpand.anchorGravity = Gravity.TOP | Gravity.CENTER;
				expandBottomToolbarButton.setImageResource(R.drawable.chevron_down);
			}
			bottomToolbar.setVisibility(visibility);
		});
	}

	private void onSettingsClicked(View view) {
		PopupMenu popupMenu = new PopupMenu(this, view);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.ar_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(this::onPopupMenuClicked);
		popupMenu.show();

	}

	private void onAddSubjectClicked(View view) {
		showAddSubjectDialog();
	}

	private void onPlayAndPauseClicked(View view) {
		if (state.equals(TestPersonState.RUNNING)) {
			state = TestPersonState.STOPPED;
			chronometer.stop();
			playAndPauseButton.setImageResource(R.drawable.play_circle_outline);
			timeIcon.setImageDrawable(getDrawable(R.drawable.timer_off));
			sensorBackgroundService.stop();
		} else {
			state = TestPersonState.RUNNING;
			chronometer.start();
			sensorBackgroundService.start();
			playAndPauseButton.setImageResource(R.drawable.pause_circle_outline);
		}
		updateState(state);

	}

	private void onCancelClicked(View view) {
		showCancelDialog(state);
		chronometer.stop();
		sensorBackgroundService.stop();
		timeIcon.setImageDrawable(getDrawable(R.drawable.timer_off));
		updateState(TestPersonState.STOPPED);
	}

	private void showCancelDialog(TestPersonState currentState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.cancel_study).setMessage(R.string.cancel_study_hint).setPositiveButton(R.string.confirm, (dialog, which) -> {
			showDirectorModus();
			hideSubjectModus();
			chronometer.setBase(SystemClock.elapsedRealtime());
			updateState(TestPersonState.IDLE);
			sensorBackgroundService.abort();
		}).setNegativeButton(R.string.no, (dialog, which) -> {
			updateState(currentState);
		}).create().show();
	}


	private void onFinishClicked(View view) {
		chronometer.stop();
		sensorBackgroundService.stop();
		updateState(TestPersonState.STOPPED);
		showFinishDialog(state);

	}

	private void showFinishDialog(TestPersonState currentState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.finish_study).setMessage(R.string.finish_study_hint).setPositiveButton(R.string.done, (dialog, which) -> {
			hideSubjectModus();
			showDirectorModus();
			startSurveyActivity();
		}).setNegativeButton(R.string.cancel, (dialog, which) -> {
			sensorBackgroundService.start();
			updateState(currentState);
			chronometer.start();
		}).create().show();
	}

	private void startSurveyActivity() {
		Intent intent = new Intent(this, SurveyActivity.class);
		intent.putExtra(SurveyActivity.PREF_KEY_STUDY_SURVEYS, study);
		startActivity(intent);
	}

	private void onTestModusClicked(View view) {

	}

	private void showDirectorModus() {
		testModusButton.setVisibility(View.VISIBLE);
		addSubjectButton.setVisibility(View.VISIBLE);
		settingsButton.setVisibility(View.VISIBLE);
	}

	private void hideDirectorModus() {
		testModusButton.setVisibility(View.GONE);
		addSubjectButton.setVisibility(View.GONE);
		settingsButton.setVisibility(View.GONE);
	}

	private void showSubjectModus() {
		cancelButton.setVisibility(View.VISIBLE);
		playAndPauseButton.setVisibility(View.VISIBLE);
		finishButton.setVisibility(View.VISIBLE);
	}

	private void hideSubjectModus() {
		cancelButton.setVisibility(View.GONE);
		playAndPauseButton.setVisibility(View.GONE);
		finishButton.setVisibility(View.GONE);
	}

	private void showAddSubjectDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.add_subject).setMessage(getString(R.string.add_subject_hint, study.getName())).setPositiveButton(R.string.add, (dialog, which) -> {
			sensorBackgroundService.initialize(study);
			showStudyStartDialog();
		}).setNegativeButton(R.string.cancel, null).create().show();
	}

	private void showStudyStartDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.are_you_ready).setMessage(R.string.subject_start_help).setPositiveButton(R.string.lets_go, (dialog, which) -> {
			hideDirectorModus();
			showSubjectModus();
			timeIcon.setImageDrawable(getDrawable(R.drawable.timer));
			chronometer.setBase(SystemClock.elapsedRealtime());
			chronometer.start();
			updateState(TestPersonState.RUNNING);
			sensorBackgroundService.start();
		}).setNegativeButton(R.string.cancel, null).create().show();
	}

	private void updateState(TestPersonState state) {
		this.state = state;
		studyStatusView.setText(state.name());
	}

	private boolean onPopupMenuClicked(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.manage_study:
				finish();
				break;
			case R.id.manage_ar_scene:
				break;
			case R.id.ar_settings:
				showArSettingDialog();
				break;
		}
		return true;
	}

	private void showArSettingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		builder.setTitle(R.string.ar_settings);
		View dialogView = inflater.inflate(R.layout.dialog_ar_setting, null);
		Spinner selectionSpinner = dialogView.findViewById(R.id.spinner_selection);
		Spinner rotationSpinner = dialogView.findViewById(R.id.spinner_rotation);
		Spinner scaleSpinner = dialogView.findViewById(R.id.spinner_scale);

		builder.setView(dialogView).setPositiveButton(R.string.apply, ((dialog, which) -> {
			/*currentSelectionTechnique = selectionSpinner.getSelectedItem().toString();
			currentRotationTechnique = rotationSpinner.getSelectedItem().toString();
			currentSelectionTechnique = scaleSpinner.getSelectedItem().toString();*/

		})).setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()));
		builder.create().show();
	}

	private void handleOnTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
		// First call ArFragment's listener to handle TransformableNodes.
		//TODO check selection technique
		arFragment.onPeekTouch(hitTestResult, motionEvent);

		// Check for touching a Sceneform node
		if (hitTestResult.getNode() != null) {
			return;
		}

		// Otherwise call gesture detector.
		trackableGestureDetector.onTouchEvent(motionEvent);
	}

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


	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			SensorBackgroundService.SensorBackgroundBinder binder = (SensorBackgroundService.SensorBackgroundBinder) service;
			sensorBackgroundService = binder.getService();
			bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bound = false;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, SensorBackgroundService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(connection);
		bound = false;
	}
}
