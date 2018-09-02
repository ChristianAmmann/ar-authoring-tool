package ncxp.de.arauthoringtool.sceneform;

import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.Node;

public class PlacementNode extends Node {

	private Material        highlight;
	private ModelRenderable frameRenderable;

	public PlacementNode(ModelRenderable frameRenderable, Material highlight) {
		this.highlight = highlight;
		this.frameRenderable = frameRenderable;
		setRenderable(frameRenderable);

	}

	public void select() {
		ModelRenderable modelRenderable = frameRenderable.makeCopy();
		modelRenderable.setMaterial(highlight);
		setRenderable(modelRenderable);
	}

	public void unselect() {
		setRenderable(frameRenderable);
	}

}
