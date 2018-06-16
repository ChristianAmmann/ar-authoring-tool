package ncxp.de.mobiledatacollection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
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

public class ArActivity extends AppCompatActivity {
	private static final String TAG = ArActivity.class.getSimpleName();

	private ArFragment      arFragment;
	private ModelRenderable andyRenderable;
	private GestureDetector trackableGestureDetector;

	@Override
	@SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
	// CompletableFuture requires api level 24
	// FutureReturnValueIgnored is not valid
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ar_activity);

		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

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
			public boolean onSingleTapUp(MotionEvent e) {
				onSingleTapUp(e);
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}
		});

	}

	private void handleOnTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
		// First call ArFragment's listener to handle TransformableNodes.
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

}