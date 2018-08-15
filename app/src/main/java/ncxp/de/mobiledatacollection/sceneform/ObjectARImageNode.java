package ncxp.de.mobiledatacollection.sceneform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class ObjectARImageNode extends Node {

	private String fileName;

	public ObjectARImageNode(String fileName, ModelRenderable object) {
		setRenderable(object);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
