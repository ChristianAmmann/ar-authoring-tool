package ncxp.de.mobiledatacollection.sceneform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class ScaleWidgetNode extends Node {

	public ScaleWidgetNode(ViewRenderable scaleControlsRenderable) {
		setRenderable(scaleControlsRenderable);
	}
}
