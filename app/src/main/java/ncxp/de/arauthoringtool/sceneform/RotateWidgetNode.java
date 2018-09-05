package ncxp.de.arauthoringtool.sceneform;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class RotateWidgetNode extends Node {

	public RotateWidgetNode(ViewRenderable rotateControlsRenderable) {
		setRenderable(rotateControlsRenderable);
	}

	@Override
	public void onUpdate(FrameTime frameTime) {
		Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
		Vector3 cardPosition = getWorldPosition();
		Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
		Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
		setWorldRotation(lookRotation);
	}
}
