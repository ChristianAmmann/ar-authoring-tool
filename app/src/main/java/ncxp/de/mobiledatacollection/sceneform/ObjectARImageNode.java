package ncxp.de.mobiledatacollection.sceneform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class ObjectARImageNode extends Node {

	public ObjectARImageNode(ModelRenderable object) {
		setRenderable(object);
	}
}
