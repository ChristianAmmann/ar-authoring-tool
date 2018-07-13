package ncxp.de.mobiledatacollection;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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

import ncxp.de.mobiledatacollection.datalogger.SensorBackgroundService;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.TestPersonState;

public class ArActivity extends AppCompatActivity {
	private static final String TAG = ArActivity.class.getSimpleName();

	public static final String KEY_STUDY = "study_key";

	private ArFragment      arFragment;
	private ModelRenderable andyRenderable;
	private GestureDetector trackableGestureDetector;

	private ImageButton     settingsButton;
	private ImageButton     expandBottomToolbarButton;
	private ImageButton     testModusButton;
	private ImageButton     addSubjectButton;
	private ImageButton     cancelButton;
	private ImageButton     playAndPauseButton;
	private ImageButton     finishButton;
	private ImageView       timeIcon;
	private LinearLayout    bottomToolbar;
	private TestPersonState state = TestPersonState.STOPPED;
	private TextView        studyStatusView;
	private Chronometer     chronometer;
	private float           displayCenterY;
	private float           displayCenterX;
	private Study           study;


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
		displayCenterX = this.getResources().getDisplayMetrics().widthPixels / 2 - 100;
		displayCenterY = this.getResources().getDisplayMetrics().heightPixels / 2;

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
		//TODO stop service
		if (state.equals(TestPersonState.RUNNING)) {
			state = TestPersonState.STOPPED;
			chronometer.stop();
			playAndPauseButton.setImageResource(R.drawable.play_circle_outline);
			timeIcon.setImageDrawable(getDrawable(R.drawable.timer_off));
		} else {
			state = TestPersonState.RUNNING;
			chronometer.start();
			playAndPauseButton.setImageResource(R.drawable.pause_circle_outline);
		}
		setTextOfState(state);

	}

	private void onCancelClicked(View view) {
		showDirectorModus();
		hideSubjectModus();
		timeIcon.setImageDrawable(getDrawable(R.drawable.timer_off));
		//TODO testperson state abort
		state = TestPersonState.IDLE;
		setTextOfState(state);
		chronometer.stop();
		chronometer.setBase(SystemClock.elapsedRealtime());
	}


	private void onFinishClicked(View view) {
		chronometer.stop();
		//TODO show survey
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
			state = TestPersonState.RUNNING;
			setTextOfState(state);
			startService();
		}).setNegativeButton(R.string.cancel, null).create().show();
	}

	private void startService() {
		Intent serviceIntent = new Intent(this, SensorBackgroundService.class);
		serviceIntent.putExtra(SensorBackgroundService.KEY_STUDY, study);
		this.startService(serviceIntent);
	}


	private void setTextOfState(TestPersonState state) {
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

}
