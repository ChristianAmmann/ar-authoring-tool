package ncxp.de.arauthoringtool.sceneform;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class ArNode extends TransformableNode {

	private String  fileName;
	private Integer qrCodeNumber;

	public ArNode(TransformationSystem transformationSystem, String fileName, ModelRenderable object) {
		super(transformationSystem);
		setRenderable(object);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public Integer getQrCodeNumber() {
		return qrCodeNumber;
	}

	public void setQrCodeNumber(Integer qrCodeNumber) {
		this.qrCodeNumber = qrCodeNumber;
	}
}
