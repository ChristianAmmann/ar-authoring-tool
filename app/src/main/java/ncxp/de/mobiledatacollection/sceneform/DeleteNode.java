package ncxp.de.mobiledatacollection.sceneform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class DeleteNode extends Node {


	public DeleteNode(ViewRenderable deleteRenderable) {
		create(deleteRenderable);
	}

	private void create(ViewRenderable deleteRenderable) {
		setRenderable(deleteRenderable);
		setLocalPosition(new Vector3(0.2f, 0.3f, 0.05f));
		//setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
	}
}
