package ncxp.de.mobiledatacollection.sceneform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class PlaceholderNode extends Node {

	public PlaceholderNode(ModelRenderable frameRenderable) {
		create(frameRenderable);
	}

	private void create(ModelRenderable frameRenderable) {
		setRenderable(frameRenderable);
		setLocalScale(new Vector3(0.3f, 0.3f, 0.4f));
	}
}
