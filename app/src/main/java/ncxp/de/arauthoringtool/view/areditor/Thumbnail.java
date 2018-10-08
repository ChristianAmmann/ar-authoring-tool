package ncxp.de.arauthoringtool.view.areditor;

import android.graphics.drawable.Drawable;

public class Thumbnail {

	private Drawable drawable;
	private String   imageName;
	private boolean  selected;

	public Thumbnail(Drawable drawable, String imageName) {
		this.drawable = drawable;
		this.imageName = imageName;
		this.selected = false;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public String getImageName() {
		return imageName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
