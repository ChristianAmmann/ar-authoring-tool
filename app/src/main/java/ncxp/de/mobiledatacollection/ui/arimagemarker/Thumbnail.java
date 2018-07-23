package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.graphics.drawable.Drawable;

public class Thumbnail {

	private Drawable drawable;
	private String   imageName;

	public Thumbnail(Drawable drawable, String imageName) {
		this.drawable = drawable;
		this.imageName = imageName;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public String getImageName() {
		return imageName;
	}
}
