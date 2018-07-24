package ncxp.de.mobiledatacollection.sceneform;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;

public class AugmentedImageAnchor extends AnchorNode {

	private AugmentedImage image;

	public void setImage(AugmentedImage image) {
		this.image = image;
		setAnchor(image.createAnchor(image.getCenterPose()));
	}

	public AugmentedImage getImage() {
		return image;
	}
}
