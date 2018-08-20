package ncxp.de.mobiledatacollection.sceneform;

import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class ObjectARImageNode extends TransformableNode {

	public static final String TAG = ObjectARImageNode.class.getSimpleName();

	private String          fileName;
	private Material        highlight;
	private ModelRenderable object;

	public ObjectARImageNode(TransformationSystem transformationSystem, String fileName, ModelRenderable object, Material highlight) {
		super(transformationSystem);
		setRenderable(object);
		this.highlight = highlight;
		this.fileName = fileName;
		this.object = object;

	}

	public String getFileName() {
		return fileName;
	}

	/*@Override
	public boolean select() {
		ModelRenderable modelRenderable = object.makeCopy();
		modelRenderable.setMaterial(highlight);
		setRenderable(modelRenderable);
		return super.select();
	}*/
}
