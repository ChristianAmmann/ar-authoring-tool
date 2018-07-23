package ncxp.de.mobiledatacollection.sceneform;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class PlaceholderNode extends Node {

	private static final float DEGREES_PER_SECONDS = 90.0f;
	private static final float SPEED_MULTIPLIER    = 1.0f;

	private Node           arrowNode;
	private ObjectAnimator animator;

	public PlaceholderNode(ModelRenderable podestRenderable, ModelRenderable arrowRenderable) {
		create(podestRenderable, arrowRenderable);
	}

	private void create(ModelRenderable podestRenderable, ModelRenderable arrowRenderable) {
		setRenderable(podestRenderable);
		setLocalScale(new Vector3(0.3f, 0.3f, 0.4f));
		arrowNode = new Node();
		arrowNode.setParent(this);
		arrowNode.setRenderable(arrowRenderable);
		arrowNode.setLocalPosition(new Vector3(0.0f, 0.015f, 0.0f));
		arrowNode.setLocalScale(new Vector3(0.4f, 0.4f, 0.45f));
	}

	@Override
	public void onActivate() {
		startAnimation();
	}

	private ObjectAnimator createAnimator() {
		Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 0);
		Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 120);
		Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 240);
		Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 360);

		ObjectAnimator orbitAnimation = new ObjectAnimator();
		orbitAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);

		// Next, give it the localRotation property.
		orbitAnimation.setPropertyName("localRotation");
		// Use Sceneform's QuaternionEvaluator.
		orbitAnimation.setEvaluator(new QuaternionEvaluator());

		//  Allow orbitAnimation to repeat forever
		orbitAnimation.setRepeatCount(ObjectAnimator.INFINITE);
		orbitAnimation.setRepeatMode(ObjectAnimator.RESTART);
		orbitAnimation.setInterpolator(new LinearInterpolator());
		orbitAnimation.setAutoCancel(true);

		return orbitAnimation;
	}

	private void startAnimation() {
		if (animator != null) {
			return;
		}
		animator = createAnimator();
		animator.setTarget(arrowNode);
		animator.setDuration(getAnimationDuration());
		animator.start();
	}


	private long getAnimationDuration() {
		return (long) (1000 * 360 / (DEGREES_PER_SECONDS * SPEED_MULTIPLIER));
	}

}
